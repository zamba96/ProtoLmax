package disruptors;

import VOs.Request;

/**
 * una casilla del Buffer de entrada
 * @author juandavid
 *
 */
public class BufferSlot {

	/**
	 * mensaje que es leido de Kafka, deberia estar en formato JSON 
	 */
	private String message;
	
	/**
	 * indica si ya fue guardado el mensaje
	 */
	private boolean journaled;
	
	/**
	 * indica si ya fue replicado el mensaje
	 */
	@Deprecated
	private boolean replicated;
	
	/**
	 * Indica si ya fue decodificado el mensaje (Convertido en un objeto Request)
	 */
	private boolean marshalled;
	
	/**
	 * indica si ya fue procesado por la logica
	 */
	private boolean processed;
	
	/**
	 * el objeto que debe ser procesado por la logica
	 */
	private Request request;
	
	/**
	 * crea un nuevo bufferSlot
	 */
	public BufferSlot() {
		journaled = false;
		replicated = true; //cambiar esto cuando se aplique;
		marshalled = false; 
		processed = true;
	}
	
	/**
	 * retorna el mensaje
	 * @return el mensaje
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * inserta un mensaje, deja las marcas como si fuera nuevo (marshall, 
	 * processed y journaled son false)
	 * @param message
	 */
	public synchronized void insertMessage(String message) {
		this.message = message;
		journaled = false;
		processed = false;
		marshalled = false;
	}
	
	/**
	 * retorna si fue guardado
	 * @return
	 */
	public boolean isJournaled() {
		return journaled;
	}
	
	/**
	 * Marca el slot como guardado
	 */
	public void journal() {
		journaled = true;
	}

	/**
	 * responde si ya esta listo para ser procesado
	 * @return true si ya esta listo para ser procesado
	 */
	public boolean isReady() {
		return (journaled && marshalled && replicated);
	}

	/**
	 * responde si ya fue procesado
	 * @return true si ya fue procesado
	 */
	public boolean isProcessed() {
		return processed;
	}

	/**
	 * setter de si fue procesado o no
	 * @param processed true o false
	 */
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	/**
	 * retorna el request del slot
	 * @return el request del slot
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * Setea el Request del Slot
	 * @param request el Request que debe ser seteado en el Slot
	 */
	public void setRequest(Request request) {
		this.request = request;
	}
	
	/**
	 * indica si fue decodificado
	 * @return true si ya fue decodificado, false dlc
	 */
	public boolean isMarshalled() {
		return marshalled;
	}

	/**
	 * marca el slot como decodificado
	 */
	public void marshall() {
		marshalled = true;
		
	}
	
	
	
	
	
}
