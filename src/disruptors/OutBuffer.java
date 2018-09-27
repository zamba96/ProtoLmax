package disruptors;

import VOs.Response;

public class OutBuffer {

	
	private OutSlot[] slots;
	
	private int n;
	
	private int addPos;
	
	@SuppressWarnings("unused")
	private int readerPos;
	
	private Object kodSync;
	
	private Object dbSync;
	
	private Object jurSync;
	
	private int kodPos;
	
	private int dbPos;

	private int jurPos;
	
	
	
	
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
		}	}
	
	
}
