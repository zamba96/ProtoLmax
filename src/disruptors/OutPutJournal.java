package disruptors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutPutJournal extends Thread {

	private int id;

	private BufferedWriter writer;

	private OutBuffer buffer;
	
	private boolean sigue;
	
	public OutPutJournal(int id, String path, OutBuffer buffer) throws IOException {
		writer = new BufferedWriter(new FileWriter(new File(path), true));
		this.id = id;
		this.buffer = buffer;
		sigue = true;
	}

	private void write(String message) throws IOException {
		//System.out.println("Write: " + message);
		synchronized(this) {
			writer.write(message);
			writer.newLine();
			writer.flush();
		}
	}

	public void run() {
		while(sigue) {

			OutSlot slot = buffer.getNextSlotJournal();
			if(slot != null && slot.getResponse() != null ){
				try {

					write(slot.getResponse().toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				slot.setJournaled(true);
			}else {
				try {
					sleep(10L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public int getJournalId() {
		return id;
	}

	public void end() {
		sigue = false;
		
	}
	

	
}
