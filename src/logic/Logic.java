package logic;

import java.util.ArrayList;
import java.util.HashMap;

import VOs.AddRequest;
import VOs.AreaRequest;
import VOs.ErrorResponse;
import VOs.ListResponse;
import VOs.Request;
import VOs.ReservaRequest;
import VOs.Response;
import VOs.TimeResponse;
import disruptors.Buffer;
import disruptors.BufferSlot;
import disruptors.OutBuffer;

/**
 * procesador de logica
 * @author juandavid
 *
 */
public class Logic extends Thread{

	/**
	 * un Hashmap donde se encuentran todos los parqueaderos
	 */
	private HashMap<Long, Parqueadero> mapa;

	/**
	 * el buffer de entrada de donde se leen los requests
	 */
	private Buffer buffer;

	/**
	 * indica si sigue la ejecucion
	 */
	private boolean sigue;

	/**
	 * el buffer donde se escriben las respuestas
	 */
	private OutBuffer bufferSalida;

	/**
	 * Hashmap con los parqueaderos libres
	 */
	private HashMap<Long, Parqueadero> libres;
	
	/**
	 * Hashmap con los parqueaderos ocupados
	 */
	private HashMap<Long, Parqueadero> ocupados;

	/**
	 * inicializa el procesador de logica
	 * @param espacios numero de espacios iniciales (para tener el HashMap listo)
	 * @param buffer el buffer de donde se leen los @Requests a procesar
	 * @param bufferSalida el buffer donde se van a escribir las @Response
	 */
	public Logic(int espacios, Buffer buffer, OutBuffer bufferSalida) {
		mapa = new HashMap<>(espacios);
		this.buffer = buffer;
		this.bufferSalida = bufferSalida;
		sigue  = true;
		
		//Crea muchos espacios nuevos, debug test
		/*
		for(Long i = 0L; i < espacios; i++) {
			mapa.put(i, new Parqueadero("Calle: " + i, i));
		}
		*/
		libres = new HashMap<>(espacios);
		ocupados = new HashMap<>(espacios/2);
		
	}

	/**
	 * agrega un espacio al sistema
	 * @param req el request con la informacion del espacio nuevo
	 * @return un response con el espacio creado
	 */
	public Response addEspacio(AddRequest req) {
		if(mapa.get(req.getEspacioId()) == null) {
		Parqueadero nu = new Parqueadero(req.getDireccion(), req.getEspacioId());
		mapa.put(nu.getId(), nu);
		libres.put(nu.getId(), nu);
		nu.setLat(req.getLat());
		nu.setLon(req.getLon());
		req.getResponse().setEspacio(nu);
		return req.getResponse();
		}else {
			return new ErrorResponse("Ya existe un Parqueadero con este id");
		}
	}

	/**
	 * ocupa un espacio
	 * @param req el Request con la info del espacio que se desea ocupar
	 * @return un Response con el espacio ocupado o con el espacio nulo si no se pudo ocupar el espacio
	 */
	public Response ocuparEspacio(Request req) {
		Parqueadero espacio = mapa.get(req.getEspacioId());
		if(espacio != null) {
			if(espacio.ocupar()) {
				req.getResponse().setEspacio(espacio);
				ocupados.put(espacio.getId(), espacio);
				libres.remove(espacio.getId());
				return req.getResponse();
			}else {
				return new ErrorResponse("El espacio con id: " + req.getEspacioId() + " esta ocupado");
			}
		}else {
			return new ErrorResponse("No existe el espacio con id:" + req.getEspacioId());
		}
	}

	/**
	 * desocupar un espacio
	 * @param req el Request con la info del espacio que desea desocupar
	 * @return Un Response con el espacio desocupado o con el espacio nulo si no se pudo ocupar el espacio
	 */
	public Response desocuparEspacio(Request req){
		Parqueadero espacio = mapa.get(req.getEspacioId());
		if(espacio != null) {
			long time = espacio.desocupar();
			if(espacio.desocupar() != 1) {
				req.getResponse().setEspacio(espacio);
				TimeResponse tr = (TimeResponse) req.getResponse();
				tr.setEspacio(espacio);
				tr.setMinutos(time);
				ocupados.remove(espacio.getId());
				libres.put(espacio.getId(), espacio);
				return tr;
			}else {
				return null;
			}
		}else {
			return new ErrorResponse("No existe el espacio con id:" + req.getEspacioId());
		}
	}

	/**
	 * Metodo que va recorriendo el Buffer y va manejando los Requests
	 */
	public void run() {
		System.out.println("Inicia Logic");
		while(sigue) {
			BufferSlot bs = buffer.getNextSlotLogic();
			if(bs == null) {
				yield();
			}else {
				Response res = manejarRequest(bs.getRequest());
				//System.out.println("Logic: " + bs.getMessage());
				//avanzar();
				bs.setProcessed(true);
				while(!bufferSalida.addResponse(res)) {
					try {
						sleep(10L);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}
		System.out.println("Logic Closed");
	}

	
	/**
	 * Maneja los requests, aca es donde se deben agregar las diferentes funcionalidades
	 * en el switch
	 * @param req Request que debe ser procesada
	 * @return la respuesta correspondiente al request
	 */
	private Response manejarRequest(Request req) {
		Response response = null;
		switch(req.getType()) {
		//Get
		case "get":
			response = get(req);
			break;
			//GetTime
		case "getTime":
			response = getTime(req);
			break;
			//Ocupar
		case "ocupar":
			response = ocuparEspacio(req);
			break;
			//desocupar
		case "desocupar":
			response = desocuparEspacio(req);
			break;
			//agregar
		case "add":
			AddRequest aReq = (AddRequest) req; 
			response = addEspacio(aReq);
			break;
		case "reserva":
			ReservaRequest rReq = (ReservaRequest) req;
			response = crearReserva(rReq);
			break;
		case "getOcupados":
			response = getOcupados(req);
			break;
		case "getDesocupados":
			response = getDesocupados(req);
			break;
		case "getArea":
			AreaRequest arReq = (AreaRequest) req;
			response = getArea(arReq);
			break;
		}
		//System.out.println("Logic.manejarMensaje(): \n" + response);
		return response;


	}
	
	private ListResponse getArea(AreaRequest arReq) {
		ListResponse lr = (ListResponse) arReq.getResponse();
		ArrayList<Parqueadero> array = new ArrayList<>();
		
		double lat = arReq.getLat();
		double lon = arReq.getLon();
		double maxDist = arReq.getDist();
		for(Parqueadero p: mapa.values()) {
			if(calcDist(lat, lon, p) < maxDist) {
				array.add(p);
			}
		}
		lr.setParqueaderos(array);
		return lr;
	}
	
	private double calcDist(double lat, double lon, Parqueadero p) {
		double lat2 = p.getLat();
		double lon2 = p.getLon();
		double dist = -1;
		double degLen = 110.25;
		double x = lat2 - lat;
		double y = (lon2 - lon) * Math.cos(lat);
		dist = degLen * Math.sqrt(x*x + y*y);
		
		
		return dist;
	}
	

	/**
	 * retorna una ListResponse con todos los parqueaderos ocupados
	 * @param req el request
	 * @return una ListResponse con todos los parqueaderos ocupados
	 */
	private ListResponse getOcupados(Request req) {
		ListResponse lr = (ListResponse) req.getResponse();
		lr.setParqueaderos(ocupados.values());
		return lr;
	}
	
	/**
	 * retorna una List response con todos los aprqueaderos desocupados
	 * @param req El request 
	 * @return una ListResponse con todos los parqueaderos desocupados
	 */
	private ListResponse getDesocupados(Request req) {
		ListResponse lr = (ListResponse) req.getResponse();
		lr.setParqueaderos(libres.values());
		return lr;
	}

	/**
	 * Crea una reserva en el sistema
	 * @param rReq el ReservaRequest que se debe procesar
	 * @return un Response con el parqueadero y la reserva nueva. @ErrorResponse 
	 * si no existe el parqueadero o la reserva no es posible porque ya esta reservado para 
	 * ese momento
	 */
	private Response crearReserva(ReservaRequest rReq) {
		Parqueadero prq = mapa.get(rReq.getEspacioId());
		if(prq != null) {
			if(prq.agregarReserva(rReq)) {
			rReq.getResponse().setEspacio(prq);
			return rReq.getResponse();
			}else {
				return new ErrorResponse("No se pudo agregar la reserva, ya se encuentra ocupado");
			}
		}else {
			return new ErrorResponse("No se encuentra el aprqueadero");
		}

	}

	/**
	 * retorna un Response con el espacio buscado
	 * @param r Request que se desea procesar	
	 * @return response con el espacio dado
	 */
	private Response get(Request req) {
		Parqueadero espacio = mapa.get(req.getEspacioId());
		Response res = req.getResponse();
		res.setEspacio(espacio);
		return res;
	}

	/**
	 * mira el tiempo transcurrido en minutos del parqueadero seleccionado
	 * @param id el id del parqueadero
	 * @return un TimeResponse con los minutos que lleva el parqueadero en uso desde que entró el carro. -1 si no hay carro aun
	 */
	private TimeResponse getTime(Request req) {
		TimeResponse tr = (TimeResponse) req.getResponse();
		Parqueadero esp = mapa.get(req.getEspacioId());
		if(esp!=null) {
			tr.setMinutos(esp.darMinutos());
			tr.setEspacio(esp);
		}else {
			tr.setEspacio(null);
			tr.setMinutos(-1);
		}
		return tr;
	}

	/**
	 * termina el thread (pues, temrina lo ultimo que estaba haciendo y luego el while
	 * se acaba)
	 */
	public void end() {
		sigue = false;
	}
	
private int cant = 0;
	
	private double start;
	
	private void avanzar() {
		cant ++;
		
		if(cant == 100) {
			double dur = System.currentTimeMillis() - start;
			double seg = (dur) /1000;
			double throughput = (double)100/seg;
			System.out.println("Logic " + "," + throughput);
			cant = 0;
		}
		if(cant == 0) {
			start = System.currentTimeMillis();
		}
	}

}
