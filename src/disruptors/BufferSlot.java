package disruptors;

import VOs.Request;

public class BufferSlot {

	
	private String message;
	
	private boolean journaled;
	
	private boolean replicated;
	
	private boolean marshalled;
	
	private boolean processed;
	
	private Request request;
	
	public BufferSlot() {
		journaled = false;
		replicated = true; //cambiar esto cuando se aplique;
		marshalled = false; 
		processed = true;
	}
	
	public String getMessage() {
		return message;
	}
	
	
	public synchronized void insertMessage(String message) {
		this.message = message;
		journaled = false;
		processed = false;
		marshalled = false;
	}
	
	public boolean isJournaled() {
		return journaled;
	}
	
	public void journal() {
		journaled = true;
	}

	public boolean isReady() {
		return (journaled && marshalled && replicated);
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
	
	public boolean isMarshalled() {
		return marshalled;
	}

	public void marshall() {
		marshalled = true;
		
	}
	
	
	
	
	
}
