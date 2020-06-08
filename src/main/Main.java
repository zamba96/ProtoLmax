package main;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import VOs.MensajeControl;
import disruptors.*;
import logic.*;

public class Main {

	public static String SERVER_ID;

	public Journal[] journals;
	
	
	public OutBuffer outBuffer;
	
	public Buffer buffer;
	
	public OutPutJournal[] outJournals;
	
	public Logic logic;
		
	public MarshallerDisruptor[] marshallers;
	
	public KafkaOutputDisruptor[] KODs;
	
	public boolean leader;
	
//	public HeartBeatSend hbs;
	
	public KafkaStreamsInputDisruptor ksid;
	
	public KafkaManualOut kmo;

	private boolean started;
	
	public static void main(String[] args) throws IOException {

		@SuppressWarnings("unused")
		Main m = new Main();
		
	}
	
	public Main() throws IOException {

		File file3 = new File("./data/");
		if(!file3.exists()) file3.mkdir();
		String path = "./data/JournalTestFile.txt";
		String path2 = "./data/JournalOut.txt";
		Properties props = new Properties();
		props.load(new FileInputStream(new File("./data/serverConfig.properties")));
		File file = new File(path);
		if(!file.exists()) file.createNewFile();
		File file2 = new File(path2);
		if(!file2.exists()) file2.createNewFile();

		
		String ip = props.getProperty("IP");
		int bufferSize = Integer.parseInt(props.getProperty("bufferSize"));
		int numInJournals = Integer.parseInt(props.getProperty("numInJournals"));
		int numOutJournals = Integer.parseInt(props.getProperty("numOutJournals"));
		int numInMarshallers = Integer.parseInt(props.getProperty("numInMarshallers"));
		int numKods = Integer.parseInt(props.getProperty("numKODS"));
		String serverId = props.getProperty("serverId");
		SERVER_ID = serverId;
		int ciclo = Integer.parseInt(props.getProperty("heartBeatCicle"));
		System.out.println("Iniciando Servidor... " + serverId);
		System.out.println("Tamano de los Buffer: " + bufferSize);
		System.out.println("Journals en la entrada: " + numInJournals);
		System.out.println("Journals en la salida: " + numOutJournals);
		System.out.println("Decodificadores en la entrada: " + numInMarshallers);
		System.out.println("Salidas a Kafka: " + numKods);
		
		
		
		//Se crean los threads
		
		buffer = new Buffer(bufferSize);
		
		ksid = new KafkaStreamsInputDisruptor("8081", ip, buffer, this);
		
		
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
		
		
		
		marshallers = new MarshallerDisruptor[numInMarshallers];
		
		for(int i = 0; i < numInMarshallers; i++) {
			marshallers[i] = new MarshallerDisruptor(buffer, i);
		}
		
		KODs = new KafkaOutputDisruptor[numKods];
		
		for(int i = 0; i < numKods; i++) {
			KODs[i] = new KafkaOutputDisruptor(outBuffer, "8081", i, leader, ip);
		}
		
		kmo = new KafkaManualOut("8081", serverId, ip);
		
		//hbs = new HeartBeatSend(ciclo, kmo);
		
		
		//Se inician los threads
		
		
		
		//logic.setPriority(Thread.MAX_PRIORITY);
		boolean deleteFilesAfterExit = false;
		Scanner sc = new Scanner(System.in);
		boolean flag = true;
		while(flag) {
			System.out.println("Escriba start para iniciar los threads "
					+ "\no stop para acabar el servidor");
			
			String st = sc.nextLine();
			if(st.equals("Exit") || st.equals("exit")) {
				flag = false;
			}else if(st.contains("delJournals")) {
				if(deleteFilesAfterExit) {
					deleteFilesAfterExit = false;
					System.out.println("Journals Will NOT be Deleted");
				}else {
					deleteFilesAfterExit = true;
					System.out.println("Journals Marked for Deletion");
				}
			}else if(st.equals("start") || st.equals("Start")) {
				if(leader) {
					leaderStart();
				}else {
					backupStart();
				}
			}
			else if(st.equals("stop")) {
				stopThreads();
			}
			
			//Iniciar independientes
			else if(st.equals("ksid start")) {
				startKSID();
			}else if(st.equals("marshall start")) {
				startMarshaller();
			}else if(st.equals("kod start")) {
				startKod();
			}else if(st.equals("outjournal start") || st.equals("out journal start")) {
				startOutJournal();
			}else if(st.equals("logic start")) {
				startLogic();
			}else if(st.equals("journal start")) {
				startJournal();
			}
			
			//Consultas
			else if(st.equals("buffer uso")) {
				System.out.println(buffer.getUsage());
			}
			
			//Registrar
			
			else if(st.equals("registrar")) {
				registrar();
			}
			
			else if(st.equals("ksid stop")) {
				stopKSID();
			}
			else if(st.equals("forceStart")){
				startThreads();
			}
			
			
			else {
				System.out.println("command not supported");
			}
		}
		
		stopThreads();
		
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
	
	/**
	 * INicia el KafkaStreams Input
	 */
	private void startKSID() {
		ksid.start();
	}
	private void stopKSID() {
		ksid.end();
	}
		
	/**
	 * Inicia los threads
	 */
	private void startThreads() {
		for(Journal j : journals) {
			j.start();
		}
		
		logic.start();
		
		//KSID
		ksid.start();
		
		for(MarshallerDisruptor m : marshallers) {
			m.start();
		}
		
		for(KafkaOutputDisruptor k : KODs) {
			k.start();
		}
		
	}
	
	/**
	 * inicia el OutJournaler
	 */
	private void startOutJournal() {
		for(OutPutJournal o: outJournals) {
			o.start();
		}
	}
	
	/**
	 * Inicia el Journaler
	 */
	private void startJournal() {
		for(Journal j: journals) {
			j.start();
		}
	}
	
	/**
	 * Inicia los KafkaOutputDisruptors
	 */
	private void startKod() {
		for(KafkaOutputDisruptor kod: KODs) {
			kod.start();
		}
	}
	
	/**
	 * Inicia la logica
	 */
	private void startLogic() {
		logic.start();
	}
	
	/**
	 * Inicia los Marshallers
	 */
	private void startMarshaller() {
		for(MarshallerDisruptor m: marshallers) {
			m.start();
		}
	}

	/**
	 * para los threads
	 */
	private void stopThreads() {
		for(Journal j: journals) {
			j.end();
		}
		
		ksid.end();
		
		logic.end();
		
		
		for(MarshallerDisruptor m: marshallers) {
			m.end();
		}
		
		for(KafkaOutputDisruptor k: KODs) {
			k.end();
		}
		
		for(OutPutJournal o: outJournals) {
			o.end();
		}
		
		//hbs.end();
		ksid.end();
		
	}
	
	
	
	public void registrar() {
		ksid.start();
		MensajeControl msg = new MensajeControl();
		msg.setServerId(SERVER_ID);
		msg.setType("registrar");
		kmo.send(msg);
	}
	
	public void manejarMensajeControl(MensajeControl msg) {
		if(msg.getServerId().equals(SERVER_ID)) {
			switch(msg.getType()) {
				case "RegistroOK":
					System.out.println("Registro OK");
					if(msg.getParam().equals("leader")) {
						//leaderStart();
						started = true;
						leader = true;
					}
					else if(msg.getParam().equals("backup")) {
						//backupStart();
						started = true;
						leader = false;
					}
					else {
						System.out.println("Error en el protocolo con el supervisor");
					}
					System.out.println("escriba start para comenzar");
					break;
				case "ping":
					MensajeControl out = new MensajeControl();
					out.setType("pong");
					out.setServerId(SERVER_ID);
					kmo.send(out);
					break;
				case "setLeader":
					System.out.println("Cambiando a modo leader...");
					startKod();
					break;
			}
		}
	}

	private void leaderStart() {
		// TODO Auto-generated method stub
		System.out.println("Iniciando en modo leader");
		startMarshaller();
		startJournal();
		startLogic();
		startKod();
		//hbs.start();
	}

	private void backupStart() {
		// TODO Auto-generated method stub
		System.out.println("Iniciando en modo backup");
		startMarshaller();
		startJournal();
		startLogic();
		//hbs.start();
	}
}
