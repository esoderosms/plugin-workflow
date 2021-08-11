package com.dotmarketing.portlets.workflows.actionlet;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dotmarketing.business.FactoryLocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.workflows.WFWConstants;
import com.dotmarketing.portlets.workflows.WorkflowUtils;
import com.dotmarketing.portlets.workflows.model.WorkflowActionClassParameter;
import com.dotmarketing.portlets.workflows.model.WorkflowActionletParameter;
import com.dotmarketing.portlets.workflows.model.WorkflowComment;
import com.dotmarketing.portlets.workflows.model.WorkflowProcessor;
import com.dotmarketing.portlets.workflows.model.WorkflowTask;
import com.dotmarketing.portlets.workflows.actionlet.WorkFlowActionlet;
import com.dotmarketing.portlets.workflows.business.WorkFlowFactory;
import com.dotmarketing.util.Logger;

import ar.com.osde.dotcms.external.services.IOsdeSecurityExternalService;
import ar.com.osde.dotcms.framework.resources.OsdeFrameworkServices;

public class MarcarFinalizado extends WorkFlowActionlet {

	private static final long	serialVersionUID	= 4111172297157414222L;
	private static IOsdeSecurityExternalService osdeSecurityService;

	public void executeAction(WorkflowProcessor processor, Map<String, WorkflowActionClassParameter> params) {

		Map<String, String> virtualParams = new HashMap<String, String>();

		virtualParams.put(WorkflowUtils.ACTUAL_ACTION_NAME, "Finalizar");
		virtualParams.put(WorkflowUtils.NEXT_STATE_NAME, "Finalizado");
		virtualParams.put(WorkflowUtils.WORKFLOW_COMMENTS, "Paso a estado virtual finalizado");
		
		//Se controla en que Steps se aplica logica para borrar el file adjunto
		if(processor.getActualStateName().equals(WFWConstants.PENDING_OF_PUBLISH_REQUEST_STEP_NAME) || processor.getActualStateName().equals(WFWConstants.PENDING_OF_ADD_PAGE_STEP_NAME)) {
			if(processor.getActualActionName().equals(WFWConstants.REJECT_ACTION_NAME) || processor.getActualActionName().equals(WFWConstants.SAVE_AND_PUBLISH_ACTION_NAME) || processor.getActualActionName().equals(WFWConstants.ADD_PAGE_ACTION_NAME)) {
				WorkflowTask task = processor.getTask();
				
				WorkFlowFactory workFlowFactory = FactoryLocator.getWorkFlowFactory();
				
				List<WorkflowComment> listWorkflowComment = null;
				
				try {
					listWorkflowComment = workFlowFactory.findWorkFlowComments(task);
				} catch (DotDataException e1) {
					Logger.error(this, e1.getMessage());
				}
				
				String lastDate = listWorkflowComment.get(0).getCreationDate().toString().split(" ")[0];
				
				List<WorkflowComment> filteredList;
				
				filteredList = listWorkflowComment.stream().filter(WorkflowComment -> lastDate.equals(WorkflowComment.getCreationDate().toString().split(" ")[0]))
				.collect(Collectors.toList());
				
				//Se guarda el comment sin el Link del file y se elimina el file del server
				for (WorkflowComment workflowComment : filteredList) {
					
					String comment = workflowComment.getComment();
					
					String partLinkComment = this.extractAttachedLink(comment);
					
					if (!partLinkComment.isEmpty() && partLinkComment != null) {
						comment = comment.replace(partLinkComment, "");
						
						workflowComment.setComment(comment);
						workflowComment.setCreationDate(new Date());
						
						try {
							workFlowFactory.saveComment(workflowComment);
							
							String path = this.getOsdeSecurityService().getFilesUploadPath().concat("/").concat(this.extractFileName(partLinkComment));
							File file = new File(path);		
							
							if (file.delete())
								Logger.info(this, "El fichero ha sido borrado satisfactoriamente");
								else
									Logger.error(this, "El fichero no puede ser borrado");
						} catch (DotDataException e1) {
							Logger.error(this, e1.getMessage());			
						}
					}					
					
				}		

			}
		}		
		
		
		try {
			processor.saveVirtualWorkflowHistoryDataDB(virtualParams);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Extrae la parte del Link adjunto del comment
	 */	
	private String extractAttachedLink(String comment) {
		String stringStartLink = "<b>Link adjunto: </b>";
		String stringEndLink = "</a><br>";
		
		int startLink = comment.indexOf(stringStartLink);
		int endLink = comment.indexOf(stringEndLink) + stringEndLink.length();
		
		if (startLink == -1 || endLink == -1)
			return "";
		else 
			return comment.substring(startLink, endLink);
	}
	
	/**
	 * Extrae el nombre del file
	 */	
	private String extractFileName(String partCommentLink) {
		String stringStartFile = "target='_blank'>";
		String stringEndFile = "</a>";
		
		int startFile = partCommentLink.indexOf(stringStartFile) + stringStartFile.length();
		int endFile = partCommentLink.indexOf(stringEndFile);
		
		return partCommentLink.substring(startFile, endFile);
	}	

	/**
	 * Descripción que se muestra al hacer click sobre esta subacción cuando es
	 * agregada en una acción del workflow.
	 */
	public String getHowTo() {
		return "Esta acción deja constancia de que el workflow ha pasado por un estado/paso virtual bajo el nombre de \"Finalizado\".";
	}

	/**
	 * Nombre que se muestra en la lista de subacciones disponibles para agregar
	 * a una acción de un workflow y para ordenarlas al momento de generar la
	 * lista.
	 */
	public String getName() {
		String nombre = "";
		String palabras[] = this.getClass().getSimpleName().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");

		for (int i = 0; i < palabras.length; i++) {
			nombre += (i != palabras.length - 1) ? palabras[i] + " " : palabras[i];
		}

		return nombre;
	}

	public List<WorkflowActionletParameter> getParameters() {
		return null;
	}
	
	public IOsdeSecurityExternalService getOsdeSecurityService() {
		if (osdeSecurityService == null) {
			this.setOsdeSecurityService(OsdeFrameworkServices.OsdeSecurityExternalService());
		}
		return osdeSecurityService;
	}

	public void setOsdeSecurityService(IOsdeSecurityExternalService osdeSecurityService) {
		MarcarFinalizado.osdeSecurityService = osdeSecurityService;
	}


}
