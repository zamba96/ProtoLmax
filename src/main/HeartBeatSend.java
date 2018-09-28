package main;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import disruptors.OutBuffer;

public class HeartBeatSend extends Thread{

	public OutBuffer buffer;
	public boolean sigue;
	private int id;


	public static String KAFKA_BROKERS = "172.24.41.149:";

	public String CLIENT_ID="client1";

	public static String TOPIC_NAME="pingDefault";

	public static String GROUP_ID_CONFIG="";

	public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;

	public static String OFFSET_RESET_LATEST="latest";

	public static String OFFSET_RESET_EARLIER="earliest";

	public static Integer MAX_POLL_RECORDS=1;


	private String port;

	private long ciclo;

	public HeartBeatSend(String port, int id, long ciclo) {
		sigue=true;
		this.port = port;
		this.id = id;
		CLIENT_ID = "KOD: " + id;
		TOPIC_NAME = "ping" + id;
		this.ciclo = ciclo;
	}

	public Producer<Long, String> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				KAFKA_BROKERS + port);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class.getName());
		return new KafkaProducer<>(props);
	}

	public void run() {
		Producer<Long, String> producer=createProducer();
		
		while(sigue) {
			//System.out.println("HeartBeat al canal: " + TOPIC_NAME);
			ProducerRecord<Long, String>  record= new ProducerRecord<Long, String>(TOPIC_NAME, "ping");
			producer.send(record);
			producer.flush();
			try {
				sleep(ciclo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		producer.close();

		System.out.println("KOD: " + id + " closed");

	}

	/**
	 * termina el while infinito
	 */
	public void end() {
		sigue = false;
	}
}
