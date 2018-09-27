package main;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import disruptors.*;
import logic.*;

public class Main {

	public static int bufferSize = 1000;
		
	public static void main(String[] args) throws IOException {

		
		String path = "./data/JournalTestFile.txt";
		File file = new File(path);
		if(!file.exists()) file.createNewFile();
		
		Buffer buffer = new Buffer(bufferSize);
		
		Journal journaler = new Journal(0, path, buffer);
		
		OutBuffer outBuffer = new OutBuffer(bufferSize);
		
		Logic logic = new Logic(1000, buffer, outBuffer);
		
		KafkaInputDisruptor kid = new KafkaInputDisruptor(buffer, "8081");
		//KafkaInputDisruptor kid2 = new KafkaInputDisruptor(buffer, "8082");
		//KafkaInputDisruptor kid3 = new KafkaInputDisruptor(buffer, "8083");
		
		MarshallerDisruptor marshaller = new MarshallerDisruptor(buffer);
		
		KafkaOutputDisruptor kod = new KafkaOutputDisruptor(outBuffer, "8081");
		KafkaOutputDisruptor kod2 = new KafkaOutputDisruptor(outBuffer, "8081");
		KafkaOutputDisruptor kod3 = new KafkaOutputDisruptor(outBuffer, "8081");
		
		journaler.start();
		logic.start();
		kid.start();
		//kid2.start();
		//kid3.start();
		marshaller.start();
		kod.start();
		kod2.start();
		kod3.start();
		
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
		kod.end();
		kod2.end();
		kod3.end();
		sc.close();
		System.out.println("Stopped");
		
		
	}

}
