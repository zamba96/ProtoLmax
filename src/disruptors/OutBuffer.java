package disruptors;

import VOs.Response;

public class OutBuffer {

	
	private OutSlot[] slots;
	
	private int n;
	
	private int addPos;
	
	private int readerPos;
	
	public OutBuffer(int n) {
		this.n = n;
		addPos = 0;
		readerPos = 0;
		slots = new OutSlot[n];
		for(int i = 0; i < n; i++) {
			slots[i] = new OutSlot();
		}
	}
	
	
	public synchronized boolean addResponse(Response res) {
		if(slots[addPos].isProcessed()) {
			slots[addPos].setResponse(res);
			addPos ++;
			if(addPos == n) addPos = 0;
			return true;
		}else {
			return false;
		}
	}
	
	
}
