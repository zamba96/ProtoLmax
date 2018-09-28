package disruptors;

import VOs.Request;

public class Buffer {

	private BufferSlot[] slots;

	private int n;

	private int journalPos;

	private int addPos;

	private int logicPos;

	private int marshallPos;

	private Object addSync;

	private Object marshSync;

	private Object jourSync;

	public Buffer(int n) {
		this.n = n;
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

	public String getSlot(int i) {
		return slots[i].getMessage();
	}

	/**
	 * retorna el siguiente BufferSlot que debe ser Jurnaled
	 * @return
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
	 * @param message
	 */
	public boolean addMessage(String message) {
		synchronized (addSync) {
			if(slots[addPos].isProcessed()) {
				slots[addPos].insertMessage(message);
				addPos ++;
				if(addPos == n) addPos = 0;
				return true;
			}else {
				return false;
			}
		}
	}

	public BufferSlot getNextSlotLogic() {
		BufferSlot r = slots[logicPos];
		if(!r.isProcessed() && r.isReady()) {
			logicPos++;
			if(logicPos == n) logicPos = 0;
			return r;
		}else {
			return null;
		}
	}

	public BufferSlot getNextMarschaller() {
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

}
