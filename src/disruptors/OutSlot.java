package disruptors;

import VOs.Response;

public class OutSlot {

	
	private boolean journaled;
	
	private boolean sent;
	
	private Response response;
	
	
	public OutSlot() {
		sent = true;
		journaled = true;
	}
	
	

	public boolean isProcessed() {
		return journaled && sent;
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
		sent = false;
		journaled = false;
	}



	public boolean isReady() {
		return response != null;
	}



	/**
	 * @return the journaled
	 */
	public boolean isJournaled() {
		return journaled;
	}



	/**
	 * @param journaled the journaled to set
	 */
	public void setJournaled(boolean journaled) {
		this.journaled = journaled;
	}



	public void setSent(boolean b) {
		// TODO Auto-generated method stub
		this.sent = b;
	}
	
	public boolean isSent() {
		return sent;
	}
	
}
