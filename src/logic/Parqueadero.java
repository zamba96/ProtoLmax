package logic;

import java.util.ArrayList;
import java.util.Date;


import VOs.ReservaRequest;

public class Parqueadero {

	private String direccion;
	
	private Long id;	
	
	private boolean disponible;
	
	private boolean reservado;
	
	private Date horaEntrada;
	
	private double lon;
	
	private double lat;
	
	
	private ArrayList<Reserva> reservas;
	
	/**
	 * crea un parqueadero con la direccion y el id dados
	 * @param direccion
	 * @param id
	 */
	public Parqueadero(String direccion, Long id) {
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
			horaEntrada = new Date();
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * retorna false si ya estaba ocupado
	 * @return true si se logra ocuar, false si ya esta ocupado
	 */
	public long desocupar() {
		if(disponible == false) {
			disponible = true;
			long r = new Date().getTime() - horaEntrada.getTime();
			horaEntrada = null;
			return r;
		}else {
			return -1;
		}
	}
	
	public long darMinutos() {
		if(horaEntrada != null) {
			long mili = System.currentTimeMillis() - horaEntrada.getTime();
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
	 * agregag una reserva al parqrueadero, revisando si esta se cruza con otras reservas del parqueadero
	 * @param rReq
	 * @return
	 */
	public boolean agregarReserva(ReservaRequest rReq) {
		rReq.setDuracion(rReq.getDuracion());
		for(Reserva r : reservas) {
			if(rReq.getFechaInicio().after(r.getFechaFin()) || rReq.getFechaFin().before(r.getFechaInicio())) {
				
			}else {
				return false;
			}
		}
		reservas.add(new Reserva(rReq));
		return true;
		
	}

	/**
	 * @return the lon
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * @param lon the lon to set
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}

	/**
	 * @return the lat
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	
}
