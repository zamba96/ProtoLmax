package disruptors;

import VOs.Response;

public class OutSlot {

	private boolean processed;
	
	private Response response;
	
	
	public OutSlot() {
		processed = true;
	}
	
	

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	/**
	 * @return the response
	 */
	public Response getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(Response response) {
		this.response = response;
	}



	public boolean isReady() {
		return response != null;
	}
	
}
