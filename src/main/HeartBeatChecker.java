package main;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import disruptors.KafkaOutputDisruptor;

public class HeartBeatChecker extends Thread{


	public boolean sigue;

	private long id;


	public static String KAFKA_BROKERS = "172.24.41.149:";

	public String CLIENT_ID="client1";

	public static String TOPIC_NAME="pingDefault";

	public static String GROUP_ID_CONFIG="consumerGroup1";

	public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;

	public static String OFFSET_RESET_LATEST="latest";

	public static String OFFSET_RESET_EARLIER="earliest";

	public static Integer MAX_POLL_RECORDS=1;


	private String port;


	/**
	 * lo que se demora el heartbeat en avisar
	 */
	private long ciclo;

	private long timeoutCiclos;

	KafkaOutputDisruptor[] KODs;

	private boolean leader;


	public HeartBeatChecker(KafkaOutputDisruptor[] KODList, String port, long otherServer, int ciclo, int timeoutCiclos) {
		leader = false;
		sigue=true;
		this.port = port;
		this.id = otherServer;
		CLIENT_ID = "HBC: " + id;
		this.ciclo = ciclo;
		this.timeoutCiclos = timeoutCiclos;
		TOPIC_NAME = "ping" + id;
		KODs = KODList;
	}


	public Consumer<Long, String> createConsumer() {

		Properties props = new Properties();

		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS + port);

		props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);

		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());

		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_POLL_RECORDS);

		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFSET_RESET_EARLIER);

		Consumer<Long, String> consumer = new KafkaConsumer<>(props);

		consumer.subscribe(Collections.singletonList(TOPIC_NAME));

		return consumer;

	}

	@SuppressWarnings("deprecation")
	public void run() {
		Consumer<Long, String> cons = createConsumer();
		System.out.println("inicia HBC con ciclo de: " + ciclo + " y numCiclos: " + timeoutCiclos);
		int numCiclosSinRespuesta = 0;
		while(sigue) {
			try {
				sleep(ciclo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ConsumerRecords<Long, String> records;
			records = cons.poll(1);
			for(ConsumerRecord<Long, String> record: records) {
				String message = record.value();
			}
			if(!records.isEmpty()) {
				numCiclosSinRespuesta = 0;
			}else {
				
				numCiclosSinRespuesta ++;
				if(numCiclosSinRespuesta > timeoutCiclos) {
					//System.out.println("HBC cambiarLider");
					cambiarALider();
					sigue = false;
				}
			}
			cons.commitAsync();
		}

		System.out.println("HBC: " + id + " closed");

	}

	private void cambiarALider() {
		if(leader == false) {
			//System.out.println("CambiarLider:Entrea");
			for(KafkaOutputDisruptor k: KODs) {
				k.setLeader(true);
			}
		}
		leader = true;
	}


	/**
	 * termina el while infinito
	 */
	public void end() {
		sigue = false;
	}

}
