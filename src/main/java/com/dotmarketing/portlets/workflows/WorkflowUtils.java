package com.dotmarketing.portlets.workflows;

import java.util.List;
import java.util.ListIterator;

import ar.com.osde.biblos.loader.WorkflowProperties;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.workflows.business.WorkflowAPI;
import com.dotmarketing.portlets.workflows.model.WorkflowAction;
import com.dotmarketing.portlets.workflows.model.WorkflowScheme;
import com.dotmarketing.portlets.workflows.model.WorkflowStep;
import com.liferay.portal.model.User;

public class WorkflowUtils {
	public static final String	SCHEME_NAME				= "Default Scheme";
	public static final String	WORKFLOW_SCHEME_NAME	= "WORKFLOW_NOMBRE_DE_ESQUEMA";
	public static final String	ACTUAL_ACTION_NAME		= "actualActionName";
	public static final String	ACTUAL_STATE_NAME		= "actualStateName";
	public static final String	NEXT_STATE_NAME			= "nextStateName";
	public static final String	WORKFLOW_COMMENTS		= "workflowComments";
	public static final String	CONTENT_CREACION_PAGINA	= "creacion_pagina_content";
	private static final String	STEP_WORKFLOW_NOT_FOUND	= "Step de Workflow no encontrado";

	public static final String SOLICITUD_DE_CREACION = "Solicitud de Creacion de Paginas";
	public static final String SOLICITUD_DE_CAMBIO = "Solicitud de Cambio";
	public static final String TIPO_DE_TRAMITE_LABEL = "Tipo de tramite: ";
	public static final String SALTO_DE_LINEA = "<br>";
	public static final String NEGRITA_INICIO = "<b>";
	public static final String NEGRITA_FIN = "</b>";
	
	public static final String WORKFLOW_PAGE_URL = "url";
	
	/**
	 * Obtiene el número del paso en base al nombre del paso por parámetro
	 * 
	 * @param steps
	 * @param stepName
	 * @return
	 * @throws Exception
	 */
	public static int getStepNumber(List<WorkflowStep> steps, String stepName) throws Exception {
		for (ListIterator<WorkflowStep> iterator = steps.listIterator(); iterator.hasNext();) {
			int stepNumber = iterator.nextIndex();
			WorkflowStep workflowStep = iterator.next();

			if (workflowStep.getName().equals(stepName)) {
				return stepNumber;
			}
		}

		throw new Exception(STEP_WORKFLOW_NOT_FOUND);
	}

	/**
	 * Obtiene la primer acción en base al nombre del paso (step) del workflow
	 * default
	 * 
	 * @param stepName
	 * @return
	 * @throws DotDataException
	 * @throws Exception
	 * @throws DotSecurityException
	 */
	public static WorkflowAction getActionByStepName(String stepName) throws DotDataException, Exception, DotSecurityException {
		User systemUser = APILocator.getUserAPI().getSystemUser();
		WorkflowAPI wapi = APILocator.getWorkflowAPI();

		WorkflowScheme ws = wapi.findSchemeByName(
				WorkflowProperties.getProperties().getProperty(WorkflowUtils.WORKFLOW_SCHEME_NAME, WorkflowUtils.SCHEME_NAME));
		List<WorkflowStep> steps = wapi.findSteps(ws);
		WorkflowStep oneStep = steps.get(WorkflowUtils.getStepNumber(steps, stepName));
		List<WorkflowAction> actions = wapi.findActions(oneStep, systemUser);
		return actions.get(0);
	}

	/**
	 * Obtiene el id de contenido para creación de página
	 * 
	 * @return
	 * @throws java.lang.Exception
	 */
	public static String getCreacionPaginaContentletID() throws Exception {

		List<Contentlet> contentlets = APILocator.getContentletAPI().search("+catchall: " + WorkflowUtils.CONTENT_CREACION_PAGINA, 0, -1, null,
				APILocator.getUserAPI().getSystemUser(), true);


		if (contentlets.isEmpty()) {
			return null;
		}

		return contentlets.get(0).getIdentifier();

	}

	public static boolean isCreacionPagina(Contentlet contentlet) throws Exception {
		String creacionPagina = getCreacionPaginaContentletID();
		return creacionPagina != null ? contentlet.getIdentifier().equals(creacionPagina) : false;
	}
}
