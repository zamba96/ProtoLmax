package disruptors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.google.gson.Gson;


/**
 * Clase que recorre el Buffer de salida, guardando los mensajes
 * Vease la documentacion del Journal
 * @author juandavid
 *
 */
public class OutPutJournal extends Thread {

	private int id;

	private BufferedWriter writer;

	private OutBuffer buffer;
	
	private boolean sigue;
	
	private Gson g;
	
	public OutPutJournal(int id, String path, OutBuffer buffer) throws IOException {
		writer = new BufferedWriter(new FileWriter(new File(path), true));
		this.id = id;
		this.buffer = buffer;
		sigue = true;
		g = new Gson();
	}

	private void write(String message) throws IOException {
		
		synchronized(this) {
			Date a = new Date();
			writer.write(a.getTime() +":" + message);
			writer.newLine();
			//writer.flush();
			//System.out.println("OutJournal: " + message);
		}
	}

	public void run() {
		while(sigue) {

			OutSlot slot = buffer.getNextSlotJournal();
			if(slot != null && !slot.isJournaled()){
				try {
					
					write(g.toJson(slot.getResponse()));
					//avanzar();
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
	
private int cant = 0;
	
	private double start;
	
	private void avanzar() {
		cant ++;
		
		if(cant == 100) {
			double dur = System.currentTimeMillis() - start;
			double seg = (dur) /1000;
			double throughput = Math.round(100/seg);
			System.out.println("OutJournal " + "," + throughput);
			cant = 0;
		}
		if(cant == 0) {
			start = System.currentTimeMillis();
		}
	}
	

	
}
