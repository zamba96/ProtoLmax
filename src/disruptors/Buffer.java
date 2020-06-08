package disruptors;

import VOs.Request;

/**
 * modela el buffer de entrada
 * @author juandavid
 *
 */
public class Buffer {

	/**
	 * Un arreglo con las casillas del Buffer
	 */
	private BufferSlot[] slots;

	/**
	 * tamano del Buffer
	 */
	private int n;

	/**
	 * siguiente posicion que el Journaler debe usar
	 */
	private int journalPos;
	/**
	 * siguiente posicion donde se debe agregar un mensaje
	 */
	private int addPos;

	/**
	 * siguiente posicion que la logica debe procesar
	 */
	private int logicPos;

	/**
	 * siguiente posicion que el marshaller debe procesar
	 */
	private int marshallPos;

	/**
	 * monitor del metodo agregar
	 */
	private Object addSync;

	/**
	 * monitor del metodo Marshall
	 */
	private Object marshSync;

	/**
	 * monitor del metodo Journal
	 */
	private Object jourSync;
	
	private int uso;

	/**
	 * Crea un nuevo buffer de tamano N, llenando sus casillas de BufferSlots nuevos y vacios
	 * @param n tamano del buffer
	 */
	public Buffer(int n) {
		this.n = n;
		uso = 0;
		journalPos = 0;
		addPos = 0;
		logicPos = 0;
		marshallPos = 0;
		slots = new BufferSlot[n];
		for(int i = 0; i < n; i++) {
			slots[i] = new BufferSlot();
		}
		addSync = new Object();
		marshSync = new Object();
		jourSync = new Object();
	}

	/**
	 * retorna la casilla en la posicion i
	 * @param i posicion de la casilla
	 * @return la casilla en la posicion i
	 */
	public String getSlot(int i) {
		return slots[i].getMessage();
	}

	/**
	 * retorna el siguiente BufferSlot que debe ser Jurnaled
	 * @return el siguiente BufferSlot que debe ser Journaled
	 */
	public BufferSlot getNextSlotJournal() {
		synchronized(jourSync) {
			BufferSlot r =  slots[journalPos];
			if(r.getMessage() != null && !r.isJournaled()) {
				journalPos ++;
				if(journalPos == n) journalPos = 0;
			}else {
				return null;
			}
			return r;
		}
	}

	/**
	 * agrega un mensaje al buffer en la siguiente posicion disponible
	 * @param message el mensaje que se debe agregar
	 */
	public boolean addMessage(String message) {
		synchronized (addSync) {
			if(slots[addPos].isProcessed()) {
				slots[addPos].insertMessage(message);
				addPos ++;
				if(addPos == n) addPos = 0;
				uso ++;
				return true;
			}else {
				return false;
			}
		}
	}

	/**
	 * retorna el siguiente BufferSlot que la logica debe procesar
	 * @return
	 */
	public BufferSlot getNextSlotLogic() {
		BufferSlot r = slots[logicPos];
		if(!r.isProcessed() && r.isMarshalled()) {
			logicPos++;
			if(logicPos == n) logicPos = 0;
			uso --;
			return r;
		}else {
			return null;
		}
	}

	/**
	 * retorna el siguiente BufferSlot que debe ser decodificado por el marshaller
	 * @return el siguiente BufferSlot que debe ser decodificado por el marshaller
	 */
	public BufferSlot getNextMarshaller() {
		synchronized (marshSync) {
			BufferSlot r =  slots[marshallPos];
			if(r.getMessage() != null && !r.isMarshalled()) {
				marshallPos ++;
				if(marshallPos == n) marshallPos = 0;
			}else {
				return null;
			}
			return r;
		}
	}

	
	public Request getNextSlotPSQL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUsage() {
		return "Buffer Usage: " + uso + " / " + n;
	}

}
