package disruptors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Clase que recorre el Buffer de entrada, guardando los mensajes
 * @author juandavid
 *
 */
public class Journal extends Thread{

	
	/**
	 * id del Jurnaler
	 */
	private int id;

	/**
	 * el writer del Journal
	 */
	private BufferedWriter writer;

	/**
	 * El buffer de donde va a leer los mensajes
	 */
	private Buffer buffer;
	
	/**
	 * indica si el Journaler sigue su ejecucion o termina en el siguiente ciclo
	 */
	private boolean sigue;
	
	/**
	 * crea un nuevo Journaler
	 * @param id el id del Journal
	 * @param path Ruta del archivo donde va a escribir
	 * @param buffer de donde se va a leer
	 * @throws IOException Si hay un error al escribir o leer el archivo
	 */
	public Journal(int id, String path, Buffer buffer) throws IOException {
		writer = new BufferedWriter(new FileWriter(new File(path), true));
		this.id = id;
		this.buffer = buffer;
		sigue = true;
	}

	/**
	 * escribe el mensaje en el archivo
	 * @param message el mensaje que quiere escribir,
	 * @throws IOException si hay un error al escribir
	 */
	private void write(String message) throws IOException {
		//System.out.println("Write: " + message);
		synchronized(this) {
			//Date a = new Date();
			writer.write(message);
			writer.newLine();
			//writer.flush();
		}
	}

	/**
	 * lee progresivamente mensajes del buffer y los escribe en el archivo
	 */
	public void run() {
		System.out.println("inicia Jurnal: " + id);
		while(sigue) {

			BufferSlot slot = buffer.getNextSlotJournal();
			if(slot != null && slot.getMessage() != null ){
				try {
					write(slot.getMessage());
					//System.out.println("Journaler: " + slot.getMessage());
					//avanzar();
				} catch (IOException e) {
					e.printStackTrace();
				}
				slot.journal();
			}else {
				try {
					sleep(10L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Termina Journal: " + id);
	}
	
	/**
	 * devuelve el id del Journal
	 * @return el id del Journal
	 */
	public int getJournalId() {
		return id;
	}

	/**
	 * termina la ejecucion del Journaler
	 */
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
			double throughput = (double)100/seg;
			System.out.println("Journal " + "," + throughput);
			cant = 0;
		}
		if(cant == 0) {
			start = System.currentTimeMillis();
		}
	}

}
