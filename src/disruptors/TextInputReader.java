package disruptors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TextInputReader extends Thread{

	
	private Buffer buffer;
	
	private String path;
	
	private int id;
	
	public TextInputReader(Buffer buffer, String path, int id) {
		this.buffer = buffer;
		this.path = path;
		this.id = id;
	}
	
	
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
	
	
	public void run() {
		try {
			readIn(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
