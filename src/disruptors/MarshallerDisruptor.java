package disruptors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import VOs.AddRequest;
import VOs.AreaRequest;
import VOs.ListResponse;
import VOs.Request;
import VOs.ReservaRequest;
import VOs.Response;
import VOs.TimeResponse;

/**
 * Clase que convierte mensajes de texto a objetos de tipo Request
 * @author juandavid
 *
 */
public class MarshallerDisruptor extends Thread{

	/**
	 * si sigue la ejecucion
	 */
	private boolean sigue;

	/**
	 * buffer de donde saca los mensajes
	 */
	private Buffer buffer;

	/**
	 * JSON Serializer 
	 */
	private Gson g;

	private int id;

	/**
	 * Crea un nuevo Decodificador
	 * @param buffer el buffer de donde se leen los mensajes a decodificar
	 */
	public MarshallerDisruptor(Buffer buffer, int id) {
		this.id = id;
		this.buffer = buffer;
		sigue = true;
		g = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();


	}
	/**
	 * lee mensajes del buffer, los convierte a Requests (de cada tipo, respectivamente)
	 */
	public void run() {
		while(sigue) {
			BufferSlot slot = buffer.getNextMarshaller();
			if(slot != null && slot.getMessage() != null ){
				String message = slot.getMessage();
				try {
					Request req = g.fromJson(message, Request.class);
					switch(req.getType()) {
					//add
					case "add":
						AddRequest arq = g.fromJson(message, AddRequest.class);
						slot.setRequest(arq);
						addEmptyResponse(arq);
						slot.marshall();
						break;
					case "reserva":
						ReservaRequest rReq = g.fromJson(message, ReservaRequest.class);
						slot.setRequest(rReq);
						addEmptyResponse(rReq);
						//System.out.println(rReq.getClass().getName());
						slot.marshall();
						break;
					case "getArea":
					case "getAreaOcupada":
					case "getAreaLibre":
						AreaRequest arReq = g.fromJson(message, AreaRequest.class);
						slot.setRequest(arReq);
						addEmptyResponse(arReq);
						slot.marshall();
						break;
					default:
						slot.setRequest(req);
						addEmptyResponse(req);
						slot.marshall();
						break;
					}
				}catch(Exception e) {
					
				}
				
				
				//avanzar();
				//System.out.println("Marshaller: " + slot.getMessage());
			}else {
				try {
					sleep(10L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Marshaller closed");

	}

	/**
	 * termina la ejecucion
	 */
	public void end() {
		sigue = false;
	}

	/**
	 * agrega la Response adecuada al request 
	 * @param req
	 */
	private void addEmptyResponse(Request req) {
		if(req.getType().equals("getTime") || req.getType().equals("desocupar")){
			TimeResponse tr = new TimeResponse();
			tr.setType(req.getType());
			tr.setId(req.getId());
			req.setResponse(tr);
		}else if(req.getType().equals("getOcupados") || req.getType().equals("getDesocupados")) {
			ListResponse ls = new ListResponse();
			ls.setType(req.getType());
			ls.setId(req.getId());
			req.setResponse(ls);
		}else if(req.getType().equals("getArea") || req.getType().equals("getAreaOcupada") || req.getType().equals("getAreaLibre")) {
			ListResponse ls = new ListResponse();
			ls.setType(req.getType());
			ls.setId(req.getId());
			req.setResponse(ls);
		}
		else {
			Response res = new Response();
			res.setType(req.getType());
			res.setId(req.getId());
			req.setResponse(res);
		}
	}

	private int cant = 0;

	private double start;

	private void avanzar() {
		cant ++;
		
		if(cant == 100) {
			double dur = System.currentTimeMillis() - start;
			double seg = (dur) /1000;
			double throughput = (double)100/seg;
			System.out.println("Marshaller " + "," + throughput);
			cant = 0;
		}
		if(cant == 0) {
			start = System.currentTimeMillis();
		}
	}
}



