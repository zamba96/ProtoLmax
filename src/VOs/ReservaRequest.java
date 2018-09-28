package VOs;

import java.util.Date;

public class ReservaRequest extends Request{

	/**
	 * fecha de la reserva
	 */
	private Date fechaInicio;
	
	private Date fechaFin;
	
	/**
	 * duracion en minutos estimada de la reserva, por defecto va a ser 120
	 */
	private Integer duracion;
	
	public ReservaRequest() {
		super();
	}
	
	/**
	 * @return the fecha
	 */
	public Date getFechaInicio() {
		return fechaInicio;
	}

	/**
	 * @param fecha the fecha to set
	 */
	public void setFechaInicio(Date fecha) {
		this.fechaInicio = fecha;
	}

	/**
	 * @return the duracion
	 */
	public Integer getDuracion() {
		return duracion;
	}

	/**
	 * @param duracion the duracion to set
	 */
	public void setDuracion(Integer duracion) {
		this.duracion = duracion;
		long millis = (duracion*60*1000)+fechaInicio.getTime();
		fechaFin = new Date(millis);
	}
	
	public Date getFechaFin() {
		return fechaFin;
	}
	
	
}
