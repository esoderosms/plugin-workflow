package com.dotmarketing.portlets.workflows.model;

import static com.dotmarketing.business.APILocator.getRoleAPI;
import static com.dotmarketing.business.APILocator.getWorkflowAPI;

import ar.com.osde.biblos.commons.CustomDBParametersLoader;
import ar.com.osde.biblos.entities.WorkflowEntity;
import ar.com.osde.biblos.reportes.ReporteWorkflow;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.Role;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.contentlet.model.ContentletDependencies;
import com.dotmarketing.portlets.htmlpageasset.model.IHTMLPage;
import com.dotmarketing.portlets.workflows.URLUtils;
import com.dotmarketing.portlets.workflows.WorkflowUtils;
import com.dotmarketing.portlets.workflows.business.DotWorkflowException;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.StringUtils;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.language.LanguageException;
import com.liferay.portal.language.LanguageUtil;
import com.liferay.portal.model.User;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
public class WorkflowProcessor {

	Contentlet contentlet;
	WorkflowAction action;
	WorkflowStep step;
	List<WorkflowStep> steps;
	WorkflowScheme scheme;
	User user;
	Role nextAssign;
	Role previousAssign;
	WorkflowStep nextStep ;
	WorkflowTask task;
	List<WorkflowHistory> history;
	String workflowMessage;
	List<WorkflowActionClass> actionClasses;
	ContentletDependencies    contentletDependencies;
	private final AtomicBoolean abort  = new AtomicBoolean(false);
	private final Map<String, Object> contextMap = new HashMap<>();

	private ConcurrentMap<String,Object> actionsContext;
	
	private String fireActionDate=""; // Fecha de ejecicion de accion/cambio de estado
	private String contentNomencId=""; // Identificador del contenido (el nomenclado)
	private String actualActionName=""; // Accion que se esta ejecutando
	private String actualStateName=""; // Estado del que sale el tramite
	private String nextStateName=""; // Estado al que entra el tramite
	private String contentHost=""; // Host
	private String fireActionUser=""; // Usuario ejecutor
	private String userFullName=""; // Nombre completo de usuario ejecutor
	
	private static final String IDENTIFICADOR_ADMINISTRACION = "identificadorAdministracin";
	private static final String GENERIC_CONTENT_TYPE = "Content (Generic)";
	private static final String TASK_ID_LABEL = "TASK_ID_LABEL";

	/**
	 * True if the processor was aborted
	 * @return boolean
	 */
	public boolean abort () {
		return this.abort.get();
	}

	/**
	 * Be carefull on calling this method, it will abort the processor of the current workflow.
	 */
	public void abortProcessor () {
		this.abort.set(true);
	}

	public ContentletDependencies getContentletDependencies() {
		return contentletDependencies;
	}

	public void setContentletDependencies(final ContentletDependencies contentletDependencies) {
		this.contentletDependencies = contentletDependencies;
	}

	/**
	 * Get the context map for this processor.
	 * @return Map
	 */
	public Map<String, Object> getContextMap() {
		return contextMap;
	}

	public List<WorkflowActionClass> getActionClasses() {
		return actionClasses;
	}

	public void setActionClasses(List<WorkflowActionClass> actionClasses) {
		this.actionClasses = actionClasses;
	}

	public String getWorkflowMessage() {
		return workflowMessage;
	}

	public void setWorkflowMessage(String workflowMessage) {
		this.workflowMessage = workflowMessage;
	}


	public Role getNextAssign() {
		return nextAssign;
	}

	public void setNextAssign(Role nextAssign) {
		this.nextAssign = nextAssign;
	}


	public Role getPreviousAssign() {
		return previousAssign;
	}

	public void setPreviousAssign(Role previousAssign) {
		this.previousAssign = previousAssign;
	}

	public WorkflowStep getNextStep() {
		return nextStep;
	}

	public void setNextStep(WorkflowStep nextStep) {
		this.nextStep = nextStep;
	}

	public List<WorkflowHistory> getHistory() {
		return history;
	}

	public void setHistory(List<WorkflowHistory> history) {
		this.history = history;
	}

	public WorkflowProcessor(final Contentlet contentlet, final User firingUser) {
       this(contentlet, firingUser, null);
	}

	public WorkflowProcessor(final Contentlet contentlet, final User firingUser, final ConcurrentMap<String,Object> actionsContext) {
		this.contentlet = contentlet;

		try {

			this.user = firingUser;

			WorkflowStep contentStep = getWorkflowAPI().findStepByContentlet(contentlet);
			if (null != contentStep) {
				scheme = getWorkflowAPI().findScheme(contentStep.getSchemeId());
			}
			
			task = getWorkflowAPI().findTaskByContentlet(contentlet);
			
			if (task == null) {
				task = new WorkflowTask();
			}

			String workflowActionId = contentlet.getActionId();
			if (UtilMethods.isSet(workflowActionId)) {
				action = findAction(contentlet, workflowActionId);
			}

			//If we found and action and we don't have a workflow we can search for it
			if (null != action && null == scheme) {
				scheme = getWorkflowAPI().findScheme(action.getSchemeId());
			}

			if (null == action) {
				Logger.error(this,"Contentlet Identifier ("+contentlet.getIdentifier()+") should not have a null workflow action");
				return;
			}

			if (UtilMethods.isSet(contentlet.getStringProperty(Contentlet.WORKFLOW_ASSIGN_KEY))) {
				nextAssign = getRoleAPI().loadRoleById(contentlet.getStringProperty(Contentlet.WORKFLOW_ASSIGN_KEY));
			}
			if(!UtilMethods.isSet(nextAssign)){
				nextAssign = getRoleAPI().loadRoleById(action.getNextAssign());
			}

			// if the action's next assign is the "System User", we assign to the user executing the workflow
			if((!UtilMethods.isSet(nextAssign)) || getRoleAPI().loadCMSAnonymousRole().getId().equals(nextAssign.getId())){
				nextAssign = getRoleAPI().loadRoleByKey(user.getUserId());
			}

			if(UtilMethods.isSet(Contentlet.WORKFLOW_COMMENTS_KEY)){
				workflowMessage = contentlet.getStringProperty(Contentlet.WORKFLOW_COMMENTS_KEY);
			}

			if (null == contentStep) {

				contentStep = this.findFirstStepByScheme (action.getSchemeId());
			}

			nextStep      = (action.isNextStepCurrentStep())?
					contentStep:getWorkflowAPI().findStep(action.getNextStep());
			step          = contentStep;
			actionClasses = getWorkflowAPI().findActionClasses(action);
			if(null == scheme) {
                scheme = getWorkflowAPI().findScheme(action.getSchemeId());
            }

			if(task != null && UtilMethods.isSet(task.getId())){
				history = getWorkflowAPI().findWorkflowHistory(task);
			}

			this.actionsContext = actionsContext;
			
			Calendar calendar = Calendar.getInstance();
			this.setFireActionDate(calendar.get(Calendar.DAY_OF_MONTH) + "/"+ calendar.get(Calendar.MONTH) + "/"+ calendar.get(Calendar.YEAR) 
					+ " "+ calendar.get(Calendar.HOUR_OF_DAY) + ":"+ calendar.get(Calendar.MINUTE) + ":"+ calendar.get(Calendar.SECOND));
			if (this.isGenericContent()) {
				this.setContentNomencId(contentlet.get(IDENTIFICADOR_ADMINISTRACION).toString());
			}
			this.setActualActionName(action.getName());
			this.setActualStateName(step.getName());
			this.setNextStateName(nextStep.getName());
			this.setFireActionUser(firingUser.getUserId());
			this.setUserFullName(firingUser.getFullName());
			Host contenletHost = APILocator.getHostAPI().find(contentlet.getHost(), APILocator.getUserAPI().getSystemUser(), false);
			this.setContentHost(contenletHost.getHostname());

		} catch (Exception e) {
			throw new DotWorkflowException(e.getMessage(),e);
		}
	}
	
	protected boolean isGenericContent() {
		return GENERIC_CONTENT_TYPE.equals(this.contentlet.getStructure().getName()) && this.contentlet.get(IDENTIFICADOR_ADMINISTRACION)!=null;
	}

	private WorkflowStep findFirstStepByScheme(final String schemeId) throws DotDataException, DotSecurityException {

		return getWorkflowAPI().findSteps(getWorkflowAPI().findScheme(schemeId)).stream().findFirst().orElse(null);
	}
	
	
	/** Guarda el historico de Workflow en una BD alternativa de ElasticSearch **/
	public void saveWorkflowHistoryDataDB() throws Exception {
		commitWorkflowHistoryDataDB(null);
	}
	
	/**
	 * Impacta un registro en Elastic de un estado/paso inexistente para el hist�rico de workflow.
	 * @param virtualParams
	 * @throws Exception
	 */
	public void saveVirtualWorkflowHistoryDataDB(Map<String,String> virtualParams) throws Exception{
		commitWorkflowHistoryDataDB(virtualParams);
	}
	
	/**
	 * Impacta un regsitro por cada cambio de estado/paso de un workflow para el histórico de workflow.
	 * @param params
	 * @throws Exception
	 */
	private void commitWorkflowHistoryDataDB(Map<String,String> params) throws Exception {
		if (this.isGenericContent()) {
			
			workflowMessage = workflowMessage!=null? workflowMessage.replaceAll("\"", "'") : "";
			if (workflowMessage.contains(TASK_ID_LABEL) && this.task != null) {
				workflowMessage = workflowMessage.replace(TASK_ID_LABEL, this.task.getId());
			}
			
			ReporteWorkflow.generarReporteWorkflow(this.armarEntityWorkflow(params));
			
			// Se elimina el logueo en ElasticSearch (-)
		}
	}
	
	/**
	* Genera la entity de workflow a guardar...
	 * @return
	 */
	private WorkflowEntity armarEntityWorkflow(Map<String, String> params){
		WorkflowEntity workflowEntity = null;
		try{
			workflowEntity = new WorkflowEntity();
			workflowEntity.setNumeroDeTramite(this.task != null ? this.task.getId() : "");
			workflowEntity.setIdContenido(this.getContentNomencId());
			workflowEntity.setNombreContenido(this.contentlet.getTitle());
			workflowEntity.setIdentifier(this.contentlet.getIdentifier());
			workflowEntity.setUsuarioMT(this.getFireActionUser());
			workflowEntity.setUsuarioNombre(this.getUserFullName());
			workflowEntity.setEstadoAnterior(this.getActualStateName());
			workflowEntity.setEstadoSiguiente((params != null ? params.get(WorkflowUtils.NEXT_STATE_NAME) : this.getNextStateName()));
			workflowEntity.setAccionActual((params != null ? params.get(WorkflowUtils.ACTUAL_ACTION_NAME) : this.getActualActionName()));
			workflowEntity.setHost(this.getContentHost());
			workflowEntity.setPaginasReferidas(this.findReferences(this.contentlet, workflowEntity));
			workflowEntity.setComentarios((params!= null ? params.get(WorkflowUtils.WORKFLOW_COMMENTS) : parsearComentarios()));

			workflowEntity.setSolicitud(esTipoSolicitud());
			workflowEntity.setAsignado(this.nextAssign != null ? this.nextAssign.getName() : "");
			String url = (String) this.contentlet.get(WorkflowUtils.WORKFLOW_PAGE_URL);
			if(!workflowEntity.isSolicitud()){
				url = "";
			} else if(url == null){
				url = getUrlPaginaSolicitud();
			}
			// La Url va a ser hasta .html sin las solapas
			workflowEntity.setPagOriginante(URLUtils.getURLWithoutFragment(URLUtils.decodeURL(url)));
		}catch(Exception e){
			e.printStackTrace();
		}
		return workflowEntity;
	}
	
	private String getUrlPaginaSolicitud() throws Exception{
		String field = "pag_originante";
		String query = "select " + field + " from REPORTE_WORKFLOW w where nro_tramite = ? " +
				" and fecha_ejecucion = (select min(fecha_ejecucion) from REPORTE_WORKFLOW " +
				" where nro_tramite = w.nro_tramite)";
		
		String url = CustomDBParametersLoader.getField(query, field, String.class, task.id);
		
		return url != null ? url : "";
	}
	
	private boolean esTipoSolicitud() throws Exception{
		String field = "wf_comment";
		String query = "select " + field + " from workflow_comment w where workflowtask_id = ? " +
					" and creation_date = (select min(creation_date) from workflow_comment " +
					" where workflowtask_id = w.workflowtask_id)";
		String comentario = null;
		if(task!=null)
		comentario = CustomDBParametersLoader.getField(query, field, String.class, task.id);
		comentario = comentario != null ? comentario : workflowMessage;
		
		String solicitudCambio = WorkflowUtils.NEGRITA_INICIO + WorkflowUtils.TIPO_DE_TRAMITE_LABEL + WorkflowUtils.NEGRITA_FIN + WorkflowUtils.SOLICITUD_DE_CAMBIO;
		String solicitudCreacion = WorkflowUtils.NEGRITA_INICIO + WorkflowUtils.TIPO_DE_TRAMITE_LABEL + WorkflowUtils.NEGRITA_FIN + WorkflowUtils.SOLICITUD_DE_CREACION;
		
		return comentario.contains(solicitudCambio) || comentario.contains(solicitudCreacion);
	}
	
	private String parsearComentarios() {
		String comentarios = this.workflowMessage;
		if (comentarios.contains("Comentario / descripcion: ")){ 
			comentarios = comentarios.substring(comentarios.indexOf("Comentario / descripcion: "));
			comentarios = comentarios.replace("Comentario / descripcion: " + WorkflowUtils.NEGRITA_FIN, "").replace(WorkflowUtils.SALTO_DE_LINEA, "").trim();
		} else {
			comentarios = "";
		}		
		
		return comentarios;
	}

	/**
	 * Searches and returns a WorkflowAction using a given workflow action id, if the Processor
	 * already have associated an action the existing action will be returned and no search will be
	 * executed.
	 */
	private WorkflowAction findAction(final Contentlet contentlet, final String workflowActionId)
			throws LanguageException {

		if (null == action) {
			try {
				action = getWorkflowAPI().findActionRespectingPermissions(workflowActionId, contentlet, this.user);
			} catch (DotSecurityException e) {
				throw new DotWorkflowException(e.getMessage(), e);
			} catch (Exception ex) {
				throw new DotWorkflowException(
						LanguageUtil.get(this.user, "message.workflow.error.invalid.action")
								+ contentlet.getActionId(), ex);
			}
		}

		return action;
	}
	
	protected String findReferences(Contentlet content, WorkflowEntity workflowEntity) throws Exception {
		ContentletAPI contentletAPI = APILocator.getContentletAPI();
		Set<String> pageReferences = new HashSet<String>();
		StringBuffer pageReferencesString = new StringBuffer();
		List<Map<String, Object>> references = contentletAPI.getContentletReferences(content,APILocator.getUserAPI().getSystemUser(),false);
		for(Map<String, Object> reference : references) {
			IHTMLPage page = (IHTMLPage)reference.get("page");
			Container container = (Container)reference.get("container"); // solapa
			String containerTitle = container.getTitle();
			String pageUrl = page.getPageUrl();
			if(workflowEntity != null){
				workflowEntity.setSolapa(containerTitle);
			} else if(containerTitle.trim() != "" && containerTitle != null ){
				pageUrl = page.getPageUrl() +" ("+containerTitle+")";
			}
			if (pageReferences.add(pageUrl)) {
				if (pageReferences.size()>1) {
					pageReferencesString.append(", ");
				}
				pageReferencesString.append(pageUrl);
			}
		}
		return pageReferencesString.toString();
	}

	public Contentlet getContentlet() {
		return contentlet;
	}

	public void setContentlet(Contentlet contentlet) {
		this.contentlet = contentlet;
	}

	public WorkflowAction getAction() {
		return action;
	}

	public void setAction(WorkflowAction action) {
		this.action = action;
	}

	public WorkflowStep getStep() {
		return step;
	}

	public void setStep(WorkflowStep step) {
		this.step = step;
	}

	public List<WorkflowStep> getSteps() {
		return steps;
	}

	public void setSteps(List<WorkflowStep> steps) {
		this.steps = steps;
	}

	public WorkflowScheme getScheme() {
		return scheme;
	}

	public void setScheme(WorkflowScheme scheme) {
		this.scheme = scheme;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public WorkflowTask getTask() {
		return task;
	}

	public void setTask(WorkflowTask task) {
		this.task = task;
	}

	public boolean inProcess(){
		return UtilMethods.isSet(action);
	}

	public boolean isRunningBulk(){
		final Boolean runningBulk = Boolean.class.cast(getContentlet().getMap().get(Contentlet.WORKFLOW_BULK_KEY));
		return runningBulk != null && runningBulk;
	}

	public ConcurrentMap<String,Object> getActionsContext() {
		return actionsContext;
	}
	
	/** Fecha de ejecicion de accion/cambio de estado */
	public String getFireActionDate() {
		return fireActionDate;
	}

	/** Fecha de ejecicion de accion/cambio de estado */
	public void setFireActionDate(String fireActionDate) {
		this.fireActionDate = fireActionDate;
	}

	/** Identificador del contenido (el nomenclado) */
	public String getContentNomencId() {
		return contentNomencId;
	}

	/** Identificador del contenido (el nomenclado) */
	public void setContentNomencId(String contentNomencId) {
		this.contentNomencId = contentNomencId;
	}

	/** Host */
	public String getContentHost() {
		return contentHost;
	}

	/** Host */
	public void setContentHost(String contentHost) {
		this.contentHost = contentHost;
	}

	/** Usuario ejecutor */
	public String getFireActionUser() {
		return fireActionUser;
	}

	/** Usuario ejecutor */
	public void setFireActionUser(String fireActionUser) {
		this.fireActionUser = fireActionUser;
	}
	
	/** Nombre completo de usuario ejecutor */
	public String getUserFullName() {
		return userFullName;
	}

	/** Nombre completo de usuario ejecutor */
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	/** Accion que se esta ejecutando */
	public String getActualActionName() {
		return actualActionName;
	}

	/** Accion que se esta ejecutando */
	public void setActualActionName(String actualActionName) {
		this.actualActionName = actualActionName;
	}

	/** Estado del que sale el tramite */
	public String getActualStateName() {
		return actualStateName;
	}

	/** Estado del que sale el tramite */
	public void setActualStateName(String actualStateName) {
		this.actualStateName = actualStateName;
	}

	/** Estado al que entra el tramite */
	public String getNextStateName() {
		return nextStateName;
	}

	/** Estado al que entra el tramite */
	public void setNextStateName(String nextStateName) {
		this.nextStateName = nextStateName;
	}
}
