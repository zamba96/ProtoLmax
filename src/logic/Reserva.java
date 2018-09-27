package logic;

import java.sql.Date;
import java.sql.Time;

public class Reserva {

	private Time hora;
	
	private Date fecha;
	
	private Usuario user;

	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}

	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the hora
	 */
	public Time getHora() {
		return hora;
	}

	/**
	 * @param hora the hora to set
	 */
	public void setHora(Time hora) {
		this.hora = hora;
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
	
}
