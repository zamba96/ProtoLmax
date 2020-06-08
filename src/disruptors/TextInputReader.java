package disruptors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase que lee un archivo de texto plano y agrega los mensajes al buffer de entrada
 * @author juandavid
 *
 */
public class TextInputReader extends Thread{

	/**
	 * el buffer de entrada
	 */
	private Buffer buffer;
	
	/**
	 * la ruta del archivo a leer
	 */
	private String path;
	
	/**
	 * el id del TIR
	 */
	private int id;
	
	/**
	 * crea un nuevo TIR
	 * @param buffer el buffer donde se van a escribir los mensajes
	 * @param path la ruta del archivo a leer
	 * @param id el id del TIR
	 */
	public TextInputReader(Buffer buffer, String path, int id) {
		this.buffer = buffer;
		this.path = path;
		this.id = id;
	}
	
	/**
	 * Lee todo un archivo, si el buffer esta lleno, duerme por 100ms y vuelve a intentar
	 * @param inPath la ruta del archivo que se quiere leer
	 * @throws IOException si hay un error de lecura
	 */
	public void readIn(String inPath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader( new File(inPath)));
		String line = reader.readLine();
		if(line != null) line += ":" + id;
		while(line != null) {
			
			boolean add = buffer.addMessage(line);
			if(!add) {
				try {
					//System.out.println("Reader: Sleep");
					sleep(100L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else {
				//System.out.println("Reader: added Message: " + line);
				line = reader.readLine();
				if(line != null) line += ":" + id;
				
			}
		}
		reader.close();
	}
	
	/**
	 * corre el lector
	 */
	public void run() {
		try {
			readIn(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
