package logic;

import java.util.HashMap;

import VOs.ErrorResponse;
import VOs.Request;
import VOs.Response;
import VOs.TimeResponse;
import disruptors.Buffer;
import disruptors.BufferSlot;
import disruptors.OutBuffer;

public class Logic extends Thread{

	private HashMap<Long, Espacio> mapa;
	
	private Buffer buffer;
	
	private boolean sigue;
	
	private OutBuffer bufferSalida;
	
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
		//Crea muchos espacios nuevos
		for(Long i = 0L; i < espacios; i++) {
			mapa.put(i, new Espacio("Calle: " + i, i));
		}
	}
	
	/**
	 * agrega un espacio al sistema
	 * @param req el request con la informacion del espacio nuevo
	 * @return un response con el espacio creado
	 */
	public Response addEspacio(Request req) {
		Espacio nu = new Espacio(req.getDireccion(), req.getId());
		mapa.put(nu.getId(), nu);
		req.getResponse().setEspacio(nu);
		return req.getResponse();
	}
	
	/**
	 * ocupa un espacio
	 * @param req el Request con la info del espacio que se desea ocupar
	 * @return un Response con el espacio ocupado o con el espacio nulo si no se pudo ocupar el espacio
	 */
	public Response ocuparEspacio(Request req) {
		Espacio espacio = mapa.get(req.getId());
		if(espacio != null) {
			if(espacio.ocupar()) {
				req.getResponse().setEspacio(espacio);
				return req.getResponse();
			}else {
				return req.getResponse();
			}
		}else {
			return new ErrorResponse("No existe el espacio con id:" + req.getId());
		}
	}
	
	/**
	 * desocupar un espacio
	 * @param req el Request con la info del espacio que desea desocupar
	 * @return Un Response con el espacio desocupado o con el espacio nulo si no se pudo ocupar el espacio
	 */
	public Response desocuparEspacio(Request req){
		Espacio espacio = mapa.get(req.getId());
		if(espacio != null) {
			if(espacio.desocupar()) {
				req.getResponse().setEspacio(espacio);
				return req.getResponse();
			}else {
				return req.getResponse();
			}
		}else {
			return new ErrorResponse("No existe el espacio con id:" + req.getId());
		}
	}
	
	/**
	 * Metodo que va recorriendo el Buffer y va manejando los Requests
	 */
	public void run() {
		while(sigue) {
			BufferSlot bs = buffer.getNextSlotLogic();
			if(bs == null) {
				try {
					sleep(10L);
				} catch (InterruptedException e) {e.printStackTrace();}
			}else {
				Response res = manejarRequest(bs);
				bs.setProcessed(true);
				while(!bufferSalida.addResponse(res)) {
					try {
						//System.out.println("duerme logic al outputtear");
						sleep(10L);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}
		System.out.println("Logic Closed");
	}

	private Response manejarRequest(BufferSlot bs) {
		Request req = bs.getRequest();
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
			response = addEspacio(req);
		}
		//System.out.println("Logic.manejarMensaje(): \n" + response);
		return response;
		
		
	}

	/**
	 * retorna un Response con el espacio buscado
	 * @param r Request que se desea procesar	
	 * @return response con el espacio dado
	 */
	private Response get(Request req) {
		Espacio espacio = mapa.get(req.getId());
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
		Espacio esp = mapa.get(req.getId());
		tr.setMinutos(esp.darMinutos());
		tr.setEspacio(esp);
		return tr;
	}

	/**
	 * termina el thread (pues, temrina lo ultimo que estaba haciendo y luego el while
	 * se acaba)
	 */
	public void end() {
		sigue = false;
	}
	
}
