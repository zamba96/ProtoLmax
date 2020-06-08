package main;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import VOs.MensajeControl;
import disruptors.KafkaManualOut;
import disruptors.OutBuffer;

public class HeartBeatSend extends Thread{

	public boolean sigue;

	private long ciclo;
	
	private KafkaManualOut kmo;
	
	

	public HeartBeatSend(long ciclo, KafkaManualOut manOut) {
		sigue=true;
		this.ciclo = ciclo;
		kmo = manOut;
	}


	public void run() {
		System.out.println("PingStart");
		while(sigue) {
			MensajeControl men = new MensajeControl();
			men.setServerId(Main.SERVER_ID);
			men.setType("ping");
			kmo.send(men);
			try {
				sleep(ciclo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

	/**
	 * termina el while infinito
	 */
	public void end() {
		sigue = false;
	}
}
