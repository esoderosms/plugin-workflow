package ar.com.osde.biblos.reportes;

import ar.com.osde.biblos.entities.WorkflowEntity;
import org.apache.logging.log4j.ThreadContext;
import com.dotmarketing.util.Logger;
import ar.com.osde.biblos.reportes.ReporteUtils;

public class ReporteWorkflow {

       public static void generarReporteWorkflow(WorkflowEntity armarEntityWorkflow) {
    	   	ReporteUtils.fixNullAttributes(armarEntityWorkflow);
    	   	ThreadContext.put("nroTramite", armarEntityWorkflow.getNumeroDeTramite());
    	   	ThreadContext.put("nombreContenido", armarEntityWorkflow.getNombreContenido());
    	   	ThreadContext.put("identifier", armarEntityWorkflow.getIdentifier());
    	   	ThreadContext.put("usuarioMT", armarEntityWorkflow.getUsuarioMT());
    	   	ThreadContext.put("usuarioNombre", armarEntityWorkflow.getUsuarioNombre());
    	   	ThreadContext.put("accion", armarEntityWorkflow.getAccionActual());
    	   	ThreadContext.put("estadoAnterior", armarEntityWorkflow.getEstadoAnterior());
    	   	ThreadContext.put("estadoSiguiente", armarEntityWorkflow.getEstadoSiguiente());
    	   	ThreadContext.put("host", armarEntityWorkflow.getHost());
    	   	ThreadContext.put("pagReferidas", armarEntityWorkflow.getPaginasReferidas());
    	   	ThreadContext.put("comentarios", armarEntityWorkflow.getComentarios());
    	   	ThreadContext.put("solicitud", armarEntityWorkflow.isSolicitud() ? "1" : "0");
    	   	ThreadContext.put("solapa", armarEntityWorkflow.getSolapa());
            ThreadContext.put("asignado", armarEntityWorkflow.getAsignado());
            ThreadContext.put("pag_originante", armarEntityWorkflow.getPagOriginante());
            Logger.info(ReporteWorkflow.class, "Se loguea el workflow"); 
       }
       
}
