package main;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import disruptors.*;
import logic.*;

public class Main {

	public static int bufferSize = 10000;
	
	public static int cantThreadsLectura = 20;
	
	public static void main(String[] args) throws IOException {

		
		String path = "./data/JournalTestFile.txt";
		String inPath = "./data/InTestFile.txt";
		File file = new File(path);
		if(!file.exists()) file.createNewFile();
		
		Buffer buffer = new Buffer(bufferSize);
		
		Journal journaler = new Journal(0, path, buffer);
		
		OutBuffer outBuffer = new OutBuffer(bufferSize);
		
		Logic logic = new Logic(1000, buffer, outBuffer);
		
		KafkaInputDisruptor kid = new KafkaInputDisruptor(buffer);
		
		MarshallerDisruptor marshaller = new MarshallerDisruptor(buffer);
		
		journaler.start();
		logic.start();
		kid.start();
		marshaller.start();
		
		Scanner sc = new Scanner(System.in);
		boolean flag = true;
		while(flag) {
			String st = sc.nextLine();
			if(st.contains("stop") || st.contains("Stop") || st.contains("Exit") || st.contains("exit")) {
				flag = false;
			}else {
				System.out.println("command not supported");
			}
		}
		journaler.end();
		logic.end();
		kid.end();
		marshaller.end();
		sc.close();
		System.out.println("Stopped");
		
		
	}

}
