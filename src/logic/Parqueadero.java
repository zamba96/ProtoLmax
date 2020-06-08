package logic;

import java.util.ArrayList;
import java.util.Date;


import VOs.ReservaRequest;

/**
 * modela un parqueadero
 * @author juandavid
 *
 */
public class Parqueadero {

	/**
	 * Direccion de un parqueadero
	 */
	private String direccion;
	
	/**
	 * id del parqueadero
	 */
	private Long id;	
	
	/**
	 * modela si esta disponible
	 */
	private boolean disponible;
	
	/**
	 * modela si esta reservado
	 */
	private boolean reservado;
	
	/**
	 * modela la hora de entrada del usuario actual
	 */
	private Date horaEntrada;
	
	/**
	 * longitud (geografica)
	 */
	private double lon;
	
	/**
	 * latitud (geografica)
	 */
	private double lat;
	
	/**
	 * lista de reservas del parqueadero
	 */
	private ArrayList<Reserva> reservas;
	
	/**
	 * crea un parqueadero con la direccion y el id dados
	 * @param direccion del parqueadero
	 * @param id del parqueadero
	 */
	public Parqueadero(String direccion, Long id) {
		this.setId(id);
		this.setDireccion(direccion);
		disponible = true;
		reservas = new ArrayList<>(2);
	}
	
	/**
	 * indica si esta disponible
	 * @return
	 */
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
	
	/**
	 * da los minutos transcurridos desde la horaEntrada
	 * @return los minutos desde la hora de entrada hasta el momendo donde se invoca el metodo
	 * -1 si no hay hora de entrada
	 */
	public long darMinutos() {
		if(horaEntrada != null) {
			long mili = System.currentTimeMillis() - horaEntrada.getTime();
			return (long) (mili/1000)/60;
		}else {
			return -1;
		}
	}

	/**
	 * 
	 * @return el id del parqueadero
	 */
	public Long getId() {
		return id;
	}

	/**
	 * sets the id
	 * @param id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * @return la direccion del parqueadero
	 */
	public String getDireccion() {
		return direccion;
	}
	/**
	 * cambia la direccion
	 * @param direccion direccion nueva
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * indica si esta reservado
	 * @return true si si, false si no
	 */
	public boolean isReservado() {
		return reservado;
	}
	
	/**
	 * cambia el estado de reserva
	 * @param reservado nuevo estado
	 */
	public void setReservado(boolean reservado) {
		this.reservado = reservado;
	}
	
	/**
	 * to string
	 */
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
