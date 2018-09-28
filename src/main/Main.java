package main;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import disruptors.*;
import logic.*;

public class Main {

	public static Journal[] journals;
	
	public static OutBuffer outBuffer;
	
	public static Buffer buffer;
	
	public static OutPutJournal[] outJournals;
	
	public static Logic logic;
	
	public static KafkaInputDisruptor kid;
	
	public static MarshallerDisruptor[] marshallers;
	
	public static KafkaOutputDisruptor[] KODs;
	
	public static boolean leader;
	
	public static HeartBeatChecker hbc;
	
	public static HeartBeatSend hbs;
		
	public static void main(String[] args) throws IOException {

		
		String path = "./data/JournalTestFile.txt";
		String path2 = "./data/JournalOut.txt";
		Properties props = new Properties();
		props.load(new FileInputStream(new File("./data/serverConfig.properties")));
		File file = new File(path);
		if(!file.exists()) file.createNewFile();
		File file2 = new File(path2);
		if(!file2.exists()) file2.createNewFile();
		File file3 = new File("./data/");
		if(!file3.exists()) file2.mkdir();
		
		
		int bufferSize = Integer.parseInt(props.getProperty("bufferSize"));
		int numInJournals = Integer.parseInt(props.getProperty("numInJournals"));
		int numOutJournals = Integer.parseInt(props.getProperty("numOutJournals"));
		int numInMarshallers = Integer.parseInt(props.getProperty("numInMarshallers"));
		int numKods = Integer.parseInt(props.getProperty("numKODS"));
		leader = Boolean.parseBoolean(props.getProperty("leader"));
		int serverId = Integer.parseInt(props.getProperty("serverId"));
		int otherServer = Integer.parseInt(props.getProperty("otherServer"));
		int ciclo = Integer.parseInt(props.getProperty("heartBeatCicle"));
		int numCiclo = Integer.parseInt(props.getProperty("heartBeatNum"));
		System.out.println("Iniciando Servidor...");
		if(leader) {
			System.out.println("En modo: leader");
		}else {
			System.out.println("En modo: backup");
		}
		System.out.println("Tamano de los Buffer: " + bufferSize);
		System.out.println("Journals en la entrada: " + numInJournals);
		System.out.println("Journals en la salida: " + numOutJournals);
		System.out.println("Decodificadores en la entrada: " + numInMarshallers);
		System.out.println("Salidas a Kafka: " + numKods);
		
		//Se crean los threads
		
		buffer = new Buffer(bufferSize);
		
		journals = new Journal[numInJournals];
		for(int i = 0; i < numInJournals; i++) {
			journals[i] = new Journal(i, path, buffer);
		}
		
		outBuffer = new OutBuffer(bufferSize);
		
		outJournals = new OutPutJournal[numOutJournals];
		for(int i = 0 ; i < numOutJournals; i++) {
			outJournals[i] = new OutPutJournal(i, path2, outBuffer);
		}
		
		logic = new Logic(1000, buffer, outBuffer);
		
		kid = new KafkaInputDisruptor(buffer, "8081", leader);
		
		marshallers = new MarshallerDisruptor[numInMarshallers];
		
		for(int i = 0; i < numInMarshallers; i++) {
			marshallers[i] = new MarshallerDisruptor(buffer);
		}
		
		KODs = new KafkaOutputDisruptor[numKods];
		
		for(int i = 0; i < numKods; i++) {
			KODs[i] = new KafkaOutputDisruptor(outBuffer, "8081", i, leader);
		}
		
		hbc = new HeartBeatChecker(KODs, "8081", otherServer, ciclo, numCiclo);
		
		hbs = new HeartBeatSend("8081", serverId, ciclo);
		
		
		//Se inician los threads
		
		
		for(Journal j : journals) {
			j.start();
		}
		
		logic.start();
		
		kid.start();
		
		for(MarshallerDisruptor m : marshallers) {
			m.start();
		}
		
		for(KafkaOutputDisruptor k : KODs) {
			k.start();
		}
		
		for(OutPutJournal o: outJournals) {
			o.start();
		}
		
		hbs.start();
		if(!leader) hbc.start();
		
		//logic.setPriority(Thread.MAX_PRIORITY);
		boolean deleteFilesAfterExit = false;
		Scanner sc = new Scanner(System.in);
		boolean flag = true;
		while(flag) {
			String st = sc.nextLine();
			if(st.contains("stop") || st.contains("Stop") || st.contains("Exit") || st.contains("exit")) {
				flag = false;
			}else if(st.contains("delJournals")) {
				if(deleteFilesAfterExit) {
					deleteFilesAfterExit = false;
					System.out.println("Journals Will NOT be Deleted");
				}else {
					deleteFilesAfterExit = true;
					System.out.println("Journals Marked for Deletion");
				}
			}
			else {
				System.out.println("command not supported");
			}
		}
		
		for(Journal j: journals) {
			j.end();
		}
		
		logic.end();
		
		kid.end();
		
		for(MarshallerDisruptor m: marshallers) {
			m.end();
		}
		
		for(KafkaOutputDisruptor k: KODs) {
			k.end();
		}
		
		for(OutPutJournal o: outJournals) {
			o.end();
		}
		
		hbs.end();
		hbc.end();
		
		sc.close();
		if(deleteFilesAfterExit) {
			for(Journal j: journals) {
				try {
					j.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(file.delete());
			System.out.println(file2.delete());
			System.out.println("Journals Deleted");
		}
		System.out.println("Stopped");
		
		
	}

}
