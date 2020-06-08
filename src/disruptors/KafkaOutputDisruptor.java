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


/**
 * Clase que Lee mensajes del OutBuffer y los manda a Kafka
 * @author David Patino
 *
 */

public class KafkaOutputDisruptor extends Thread{

	public OutBuffer buffer;
	public boolean sigue;
	private int id;


	/**
	 * IP del servidor Kafka
	 */
	public static String KAFKA_BROKERS = "172.24.41.149:";

	public String CLIENT_ID="client1";

	/**
	 * canal a donde se mandan los mensajes
	 */
	public static String TOPIC_NAME="testChannel2";

	public static String GROUP_ID_CONFIG="consumerGroup1";

	public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;

	public static String OFFSET_RESET_LATEST="latest";

	public static String OFFSET_RESET_EARLIER="earliest";

	public static Integer MAX_POLL_RECORDS=1;

	/**
	 * JSON converter
	 */
	private Gson gson;

	/**
	 * puerto del servidor
	 */
	private String port;

	/**
	 * si se corre en modo leader o no
	 */
	private boolean leader;

	private String ip;

	/**
	 * Crea un nuevo KOD
	 * @param buffer el buffer de donde se leen las respuestas
	 * @param port puerto del Servidor Kafka
	 * @param id id del KOD
	 * @param lead si se corre en modo leader o no
	 * Si es modo lider, si se van a eníar los mensajes, si no, no se envía nada
	 * @param ip 
	 */
	public KafkaOutputDisruptor(OutBuffer buffer, String port, int id, boolean lead, String ip) {
		this.ip = ip;
		this.buffer=buffer;
		sigue=true;
		gson = new Gson();
		this.port = port;
		this.id = id;
		CLIENT_ID = "KOD: " + id;

	}

	/**
	 * crea un KafkaProducer
	 * @return un nuevo Kafka Producer
	 */
	public Producer<String, String> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				ip+":" + port);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class.getName());
		return new KafkaProducer<>(props);
	}

	/**
	 * Lee respuestas del OutBuffer y las envia por el canal activo de Kafka
	 */
	public void run() {
		System.out.println("KOD: Start");
		Producer<String, String> producer=createProducer();
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
				ProducerRecord<String, String>  record= new ProducerRecord<String, String>(TOPIC_NAME, rs.getId()+"", jsonString);
				producer.send(record);
				//System.out.println("KOD Leader: " + jsonString);
				os.setSent(true);
				//avanzar();
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

	private int cant = 0;

	private double start;

	private void avanzar() {
		cant ++;

		if(cant == 100) {
			double dur = System.currentTimeMillis() - start;
			double seg = (dur) /1000;
			double throughput = (double)100/seg;
			System.out.println("KOD " + "," + throughput);
			cant = 0;
		}
		if(cant == 0) {
			start = System.currentTimeMillis();
		}
	}

}
