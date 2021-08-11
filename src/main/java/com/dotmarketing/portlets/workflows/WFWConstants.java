package com.dotmarketing.portlets.workflows;

public interface WFWConstants {
	
	public static final String WORK_FLOW_SYSTEM = "System Workflow";
    public static final String WORK_FLOW_CHANGE_REQUEST = "Solicitud De Cambio";

	/* Steps */
    public static final String START_STEP_CHANGE_REQUEST = "Iniciado";    
    public static final String START_STEP_WORKFLOW_SYSTEM = "Nuevo";
    
    public static final String PENDING_OF_PUBLISH_STEP_NAME = "Pendiente de Publicacion (contenido)";
    public  static final String PENDING_OF_PUBLISH_REQUEST_STEP_NAME = "Pendiente de Publicacion (solicitud)";
    
    public  static final String PENDING_OF_VALIDATION_STEP_NAME = "Pendiente de validacion";
    public  static final String REQUEST_MODIFY_STEP_NAME = "Solicitar Modificacion";
    public  static final String REQUEST_ADD_PAGE_STEP_NAME = "Solicitar creación de página";
    public  static final String PENDING_OF_ADD_PAGE_STEP_NAME = "Pendiente de creacion de pagina";
    public  static final String PENDING_OF_VALIDATION_PAGE_STEP_NAME = "Pendiente de validacion de pagina";
    
    public static final String UNPUBLISH_STEP_NAME = "Despublicar";
    public static final String PUBLISH_STEP_NAME = "Publicar (Resuelto)";
    
    public static final String ARCHIVE_STEP_NAME = "Archivar";
    public static final String CURRENT_STEP_NAME = "Paso actual";
    

    /* Actions */
    public static final String REQUEST_MODIFY_ACTION_NAME = "Solicitar Modificacion";
    public static final String REMOVE_CONTENT_ACTION_NAME = "Eliminar Contenido";
    public static final String SAVE_FOR_WORK_ACTION_NAME = "Guardar para Trabajar";
    public static final String REQUEST_VALIDATION_ACTION_NAME = "Solicitar validacion";
    public static final String SAVE_AND_PUBLISH_ACTION_NAME = "Guardar y Publicar";
    public static final String REJECT_ACTION_NAME = "Desestimar";
    public static final String REQUEST_PUBLISH_ACTION_NAME = "Solicitar Publicacion"; 
    public static final String RETURN_TO_CONTENTS_ACTION_NAME = "Devolver a contenidos";
    public static final String REQUEST_ADD_OF_PAGE_ACTION_NAME = "Solicitar creación de página";
    public static final String ADD_PAGE_ACTION_NAME = "Crear pagina"; 
    public static final String PUBLISH_ACTION_NAME = "Publicar";
    public static final String UNPUBLISH_ACTION_NAME = "Despublicar";    
    public static final String REQUEST_VALIDATION_OF_PAGE_ACTION_NAME = "Solicitar validación de página";    
    public static final String SAVE_ACTION_NAME = "Guardar";
    public static final String ARCHIVE_ACTION_NAME = "Archivar";
    public static final String COPY_ACTION_NAME = "Copiar";
    public static final String REPUBLISH_ACTION_NAME = "Republicar";
    public static final String UNARCHIVE_ACTION_NAME = "Desarchivar";
    public static final String DELETE_ACTION_NAME = "Eliminar";
    public static final String DESTROY_ACTION_NAME = "Destruir";
    
    
    /* Subactions */
	public static final String SAVE_AS_DRAFT_SUBACTION = "Save Draft content";
	public static final String PUBLISH_CONTENT_SUBACTION = "Publish content";
	public static final String UNLOCK_CONTENT_SUBACTION = "Unlock content";	
	public static final String VELOCITY_SCRIPT_ACTIONLET_SUBACTION = "Velocity Script Actionlet";
	public  static final String SAVE_CONTENT_SUBACTION = "Save content";
	public  static final String ARCHIVE_SUBACTION = "Archive content";
	public  static final String UNARCHIVE_SUBACTION = "Unarchive content";
	public  static final String UNPUBLISH_CONTENT_SUBACTION = "Unpublish content";
	public  static final String DELETE_CONTENT_SUBACTION = "Unpublish content";
	public  static final String RESET_WORKFLOW_SUBACTION = "Reset Workflow";
	public  static final String MARK_COMPLETE_SUBACTION = "Marcar Finalizado";
	public  static final String REMOVE_SUBACTION= "Delete content";    
	public  static final String LOCK_CONTENT_SUBACTION = "Lock content";
	public  static final String COPY_CONTENT_SUBACTION = "Copy Contentlet";    
	public  static final String NOTIFY_ASSIGNEE_SUBACTION = "Notify Assignee";
	public  static final String DESTROY_SUBACTION = "Destroy content";
	
	public  static final String RESET_WORKFLOWSUBACTION = "Reset Workflow";
	
	public static final String REQUIRE_MULTIPLE_APPROVES = "Require Multiple Approvers";	
	
	
		

}
