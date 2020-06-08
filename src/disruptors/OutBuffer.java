package disruptors;

import VOs.Response;
/**
 * Modela el buffer de salida
 * @author juandavid
 *
 */
public class OutBuffer {

	/**
	 * Un arreglo con las casillas del Buffer
	 */
	private OutSlot[] slots;


	/**
	 * tamano del Buffer
	 */
	private int n;

	/**
	 * siguiente posicion donde se debe agregar un mensaje
	 */
	private int addPos;

	@SuppressWarnings("unused")
	private int readerPos;

	/**
	 * monitor
	 */
	private Object kodSync;

	/**
	 * monitor
	 */
	private Object dbSync;

	/**
	 * monitor
	 */
	private Object jurSync;

	/**
	 * posicion del siguiente mensaje para ser mandado a kafka
	 */
	private int kodPos;

	/**
	 * posicion del siguiente mensaje que debe ser procesado por la base de datos
	 */
	private int dbPos;

	/**
	 * posicion del siguiente mensaje que sera guardado en un archivo
	 */
	private int jurPos;

	/**
	 * crea un nuevo OutBuffer con n slots vacios
	 * @param n tamano del nuevo Buffer
	 */
	public OutBuffer(int n) {
		this.n = n;
		addPos = 0;
		readerPos = 0;
		kodPos = 0;
		dbPos = 0;
		jurPos = 0;
		kodSync = new Object();
		dbSync = new Object();
		jurSync = new Object();
		kodSync = new Object();
		slots = new OutSlot[n];
		for(int i = 0; i < n; i++) {
			slots[i] = new OutSlot();
		}
	}

	/**
	 * agrega una respuesta al siguiente slot disponible
	 * @param res respuesta a ser agregada
	 * @return true si se pudo agregar, false si no hay slots disponibles
	 */
	public synchronized boolean addResponse(Response res) {
		//System.out.println("Agrega Response: " + res);
		if(slots[addPos].isProcessed()) {
			slots[addPos].setResponse(res);
			addPos ++;
			if(addPos == n) addPos = 0;
			return true;
		}else {
			return false;
		}
	}

	/**
	 * Devuelve el siguiente slot para ser enviado a Kafka
	 * @return el siguiente slot para ser enviado a Kafka
	 * null si no hay nada disponible
	 */
	public OutSlot getNextSlotKOD() {
		synchronized (kodSync) {
			if(slots[kodPos].isReady()) {
				OutSlot r = slots[kodPos];
				kodPos++;
				if(kodPos == n) kodPos = 0;
				return r;
			}else {
				return null;
			}

		}
	}


	/**
	 * La siguiente Response que va a ser procesada por la salida SQL
	 * @return La siguiente Response que va a ser procesada por la salida SQL
	 * null si no hay nada disponible
	 */
	public Response getNextSlotPSQL() {
		synchronized (dbSync) {
			if(slots[dbPos].isReady()) {
				OutSlot r = slots[dbPos];
				dbPos++;
				if(dbPos == n) dbPos = 0;
				return r.getResponse();
			}else {
				return null;
			}

		}
	}

	/**
	 * Retorna el siguiente slot que sera guardado en el journal de salida
	 * @return el siguiente slot para se guardado en el jurnal de salida
	 * null si no hay nada disponible
	 */
	public OutSlot getNextSlotJournal() {
		synchronized(jurSync) {
			if(slots[jurPos].isReady()) {
				OutSlot r = slots[jurPos];
				jurPos++;
				if(jurPos == n) jurPos = 0;
				return r;
			}else {
				return null;
			}
		}	
	}


}
