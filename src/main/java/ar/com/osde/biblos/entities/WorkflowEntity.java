package ar.com.osde.biblos.entities;

import ar.com.osde.framework.entities.FrameworkEntity;

/**
* Entitiy para guardar en los logs : Reporte workflow...
*/
public class WorkflowEntity implements FrameworkEntity {

       private static final long serialVersionUID = 603913556017853303L;

       private String numeroDeTramite;
       private String idContenido;
       private String identifier;
       private String nombreContenido;
       private String usuarioMT;
       private String usuarioNombre;
       private String estadoAnterior;
       private String estadoSiguiente;
       private String accionActual;
       private String host;
       private String paginasReferidas;
       private String comentarios;
       private String solapa;
       private String asignado;
       private String pagOriginante;
       private Boolean solicitud;

       public String getNumeroDeTramite() {
             return numeroDeTramite;
       }

       public void setNumeroDeTramite(String numeroDeTramite) {
             this.numeroDeTramite = numeroDeTramite;
       }

       public String getIdContenido() {
             return idContenido;
       }

       public void setIdContenido(String idContenido) {
             this.idContenido = idContenido;
       }

    public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getNombreContenido() {
             return nombreContenido;
       }

       public void setNombreContenido(String nombreContenido) {
             this.nombreContenido = nombreContenido;
       }

       public String getEstadoAnterior() {
             return estadoAnterior;
       }

       public void setEstadoAnterior(String estadoAnterior) {
             this.estadoAnterior = estadoAnterior;
       }

       public String getEstadoSiguiente() {
             return estadoSiguiente;
       }

       public void setEstadoSiguiente(String estadoSiguiente) {
             this.estadoSiguiente = estadoSiguiente;
       }

       public String getAccionActual() {
             return accionActual;
       }

       public void setAccionActual(String accionActual) {
             this.accionActual = accionActual;
       }

       public String getHost() {
             return host;
       }

       public void setHost(String host) {
             this.host = host;
       }

       public String getPaginasReferidas() {
             return paginasReferidas;
       }

       public void setPaginasReferidas(String paginasReferidas) {
             this.paginasReferidas = paginasReferidas;
       }

       public String getComentarios() {
             return comentarios;
       }

       public void setComentarios(String comentarios) {
             this.comentarios = comentarios;
       }

       public String getUsuarioMT() {
             return usuarioMT;
       }

       public void setUsuarioMT(String usuarioMT) {
             this.usuarioMT = usuarioMT;
       }

	   public String getUsuarioNombre() {
		return usuarioNombre;
	}

	public void setUsuarioNombre(String usuarioNombre) {
		this.usuarioNombre = usuarioNombre;
	}

	public Boolean isSolicitud() {
			return solicitud;
	   }
	
	   public void setSolicitud(Boolean solicitud) {
			this.solicitud = solicitud;
	   }

	public String getSolapa() {
		return solapa;
	}

	public void setSolapa(String solapa) {
		this.solapa = solapa;
	}

	public String getAsignado() {
		return asignado;
	}

	public void setAsignado(String asignado) {
		this.asignado = asignado;
	}

	public String getPagOriginante() {
		return pagOriginante;
	}

	public void setPagOriginante(String pagOriginante) {
		this.pagOriginante = pagOriginante;
	}

}
