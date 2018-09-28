package disruptors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import VOs.AddRequest;
import VOs.ListResponse;
import VOs.Request;
import VOs.ReservaRequest;
import VOs.Response;
import VOs.TimeResponse;

public class MarshallerDisruptor extends Thread{


	private boolean sigue;

	private Buffer buffer;

	private Gson g;

	public MarshallerDisruptor(Buffer buffer) {
		this.buffer = buffer;
		sigue = true;
		g = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
		

	}

	public void run() {
		while(sigue) {
			BufferSlot slot = buffer.getNextMarschaller();
			if(slot != null && slot.getMessage() != null ){
				String message = slot.getMessage().split("===")[1];
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
				default:
					slot.setRequest(req);
					addEmptyResponse(req);
					slot.marshall();
					break;
				}
				//System.out.println("Marshaller: "+message+":" + req);
				
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

	public void end() {
		sigue = false;
	}

	private void addEmptyResponse(Request req) {
		if(req.getType().equals("getTime") || req.getType().equals("desocupar")){
			TimeResponse tr = new TimeResponse();
			tr.setType(req.getType());
			req.setResponse(tr);
		}else if(req.getType().equals("getOcupados") || req.getType().equals("getDesocupados")) {
			ListResponse ls = new ListResponse();
			ls.setType(req.getType());
			req.setResponse(ls);
		}
		else {
			Response res = new Response();
			res.setType(req.getType());
			req.setResponse(res);
		}
	}
}



