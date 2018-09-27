package logic;

import java.util.ArrayList;

import org.apache.kafka.common.utils.Time;

public class Espacio {

	private String direccion;
	
	private Long id;	
	
	private boolean disponible;
	
	private boolean reservado;
	
	private Time horaEntrada;
	
	private int duracion;
	
	private ArrayList<Reserva> reservas;
	
	public Espacio(String direccion, Long id) {
		this.setId(id);
		this.setDireccion(direccion);
		disponible = true;
		reservas = new ArrayList<>(2);
		
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

	/**
	 * @return the reservas
	 */
	public ArrayList<Reserva> getReservas() {
		return reservas;
	}

	/**
	 * @return the duracion
	 */
	public int getDuracion() {
		return duracion;
	}

	/**
	 * @param duracion the duracion to set
	 */
	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}
	
	
}
