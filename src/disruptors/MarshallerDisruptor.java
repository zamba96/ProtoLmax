package disruptors;

import com.google.gson.Gson;

import VOs.Request;
import VOs.Response;
import VOs.TimeResponse;

public class MarshallerDisruptor extends Thread{


	private boolean sigue;

	private Buffer buffer;

	private Gson g;

	public MarshallerDisruptor(Buffer buffer) {
		this.buffer = buffer;
		sigue = true;
		g = new Gson();

	}

	public void run() {
		while(sigue) {
			BufferSlot slot = buffer.getNextMarschaller();
			if(slot != null && slot.getMessage() != null ){
				String message = "{" + slot.getMessage().split("\\{")[1];
				Request req = g.fromJson(message, Request.class);
				//System.out.println("Marshaller: " + req);
				slot.setRequest(req);
				addEmptyResponse(req);
				slot.marshall();
			}else {
				try {
					sleep(10L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void end() {
		sigue = false;
	}

	private void addEmptyResponse(Request req) {
		if(req.getType().equals("getTime")){
			TimeResponse tr = new TimeResponse();
			tr.setType(req.getType());
			req.setResponse(tr);
		}else {
			Response res = new Response();
			res.setType(req.getType());
			req.setResponse(res);
		}
	}
}



