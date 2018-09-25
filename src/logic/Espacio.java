package logic;

import org.apache.kafka.common.utils.Time;

public class Espacio {

	private String direccion;
	
	private Long id;	
	
	private boolean disponible;
	
	private boolean reservado;
	
	private Time horaEntrada;
	
	public Espacio(String direccion, Long id) {
		this.setId(id);
		this.setDireccion(direccion);
		disponible = true;
		
	}
	
	public boolean esDisponible() {
		return disponible;
	}
	
	/**
	 * Ocupa el espacio, guarda la hora actual como hora de entrada
	 * @return true si se logra ocuar, false si ya esta ocupado
	 */
	public boolean ocupar() {
		if(disponible == true) {
			disponible = false;
			horaEntrada = Time.SYSTEM;
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * retorna false si ya estaba ocupado
	 * @return true si se logra ocuar, false si ya esta ocupado
	 */
	public boolean desocupar() {
		if(disponible == false) {
			disponible = true;
			horaEntrada = null;
			return true;
		}else {
			return false;
		}
	}
	
	public long darMinutos() {
		if(horaEntrada != null) {
			long mili = horaEntrada.milliseconds() - Time.SYSTEM.milliseconds();
			return (long) (mili/1000)/60;
		}else {
			return -1;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public boolean isReservado() {
		return reservado;
	}

	public void setReservado(boolean reservado) {
		this.reservado = reservado;
	}
	
	public String toString() {
		return "Id: " + id + " direccion: " + direccion;
	}
	
	
}
