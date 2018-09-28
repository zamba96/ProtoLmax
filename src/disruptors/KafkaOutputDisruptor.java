package disruptors;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.google.gson.Gson;

import VOs.Response;




public class KafkaOutputDisruptor extends Thread{

	public OutBuffer buffer;
	public boolean sigue;
	private int id;


	public static String KAFKA_BROKERS = "172.24.41.149:";

	public String CLIENT_ID="client1";

	public static String TOPIC_NAME="testChannel2";

	public static String GROUP_ID_CONFIG="consumerGroup1";

	public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;

	public static String OFFSET_RESET_LATEST="latest";

	public static String OFFSET_RESET_EARLIER="earliest";

	public static Integer MAX_POLL_RECORDS=1;

	private Gson gson;

	private String port;

	private boolean leader;

	public KafkaOutputDisruptor(OutBuffer buffer, String port, int id, boolean lead) {
		this.buffer=buffer;
		sigue=true;
		gson = new Gson();
		this.port = port;
		this.id = id;
		CLIENT_ID = "KOD: " + id;
		leader = lead;
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
			OutSlot os=buffer.getNextSlotKOD();
			if(os == null || os.isSent()) {
				try {
					sleep(10L);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
			else {
				Response rs= os.getResponse();
				String jsonString = gson.toJson(rs);
				ProducerRecord<Long, String>  record= new ProducerRecord<Long, String>(TOPIC_NAME, jsonString);
				if(leader) {
					producer.send(record);
					//System.out.println("KOD Leader: " + jsonString);
				}else {
					//System.out.println("KOD Non-leader: " + jsonString);
				}
				os.setSent(true);
			}
		}
		producer.flush();
		producer.close();

		System.out.println("KOD: " + id + " closed");

	}

	/**
	 * termina el while infinito
	 */
	public void end() {
		sigue = false;
	}

	/**
	 * @return the leader
	 */
	public boolean isLeader() {
		return leader;
	}

	/**
	 * @param leader the leader to set
	 */
	public void setLeader(boolean leader) {
		System.out.println("Cambia el lider a este");
		this.leader = leader;
	}

}
