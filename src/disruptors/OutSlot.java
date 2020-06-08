package disruptors;

import VOs.Response;

/**
 * Modela una casilla del Output Buffer
 * @author juandavid
 *
 */
public class OutSlot {

	/**
	 * indica si ya fue guardado en el archivo de texto
	 */
	private boolean journaled;
	
	/**
	 * indica si ya fue enviado a Kafka
	 */
	private boolean sent;
	
	/**
	 * La respuesta 
	 */
	private Response response;
	
	/**
	 * crea una nueva casilla
	 */
	public OutSlot() {
		sent = true;
		journaled = true;
	}
	
	
	/**
	 * retorna si ya fue procesada, lo cual implica que fue guardada y mandada
	 * @return true si ya fue procesada, false si no
	 */
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


	/**
	 * indica si esta listo para ser enviado 
	 * @return
	 */
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


	/**
	 * pone el estado de envio	
	 * @param b el nuevo estado de envio
	 */
	public void setSent(boolean b) {
		// TODO Auto-generated method stub
		this.sent = b;
	}
	
	/**
	 * indica si fue enviado
	 * @return
	 */
	public boolean isSent() {
		return sent;
	}
	
}
