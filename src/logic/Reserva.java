package logic;

import java.util.Date;

import VOs.ReservaRequest;

public class Reserva {

	private Date fechaInicio;
	
	private Date fechaFin;

	private Usuario user;
	
	/**
	 * duracion en minutos
	 */
	private int duracion;

	public Reserva(ReservaRequest rReq) {
		fechaInicio = rReq.getFechaInicio();
		fechaFin = rReq.getFechaFin();
		duracion = rReq.getDuracion();
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
	 * @return the user
	 */
	public Usuario getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(Usuario user) {
		this.user = user;
	}

	/**
	 * @return the duracion
	 */
	public int getDuracion() {
		return duracion;
	}

	/**
	 * guarda la duracion de la reserva y actualiza la fecha fin para que sea la fecha inicio mas la duracion
	 * @param duracion the duracion to set
	 */
	public void setDuracion(int duracion) {
		this.duracion = duracion;
		long millis = (duracion*60*1000)+fechaInicio.getTime();
		fechaFin = new Date(millis);
		
	}

	/**
	 * @return the fechaFin
	 */
	public Date getFechaFin() {
		return fechaFin;
	}

	/**
	 * @param fechaFin the fechaFin to set
	 */
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

}
