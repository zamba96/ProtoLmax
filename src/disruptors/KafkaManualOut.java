package disruptors;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.google.gson.Gson;

import VOs.MensajeControl;


public class KafkaManualOut {

	public boolean sigue;
	private String id;


	/**
	 * IP del servidor Kafka
	 */
	public static String KAFKA_BROKERS = "172.24.41.149:";

	public String CLIENT_ID="Supervisor";

	/**
	 * canal a donde se mandan los mensajes
	 */
	public static String TOPIC_NAME="SupervisorInChannel";

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
	
	private String ip;
	
	private Producer<String, String> producer;

	/**
	 * Crea un nuevo KOD
	 * @param buffer el buffer de donde se leen las respuestas
	 * @param port puerto del Servidor Kafka
	 * @param id id del KOD
	 * @param lead si se corre en modo leader o no
	 * Si es modo lider, si se van a eníar los mensajes, si no, no se envía nada
	 * @param ip 
	 */
	public KafkaManualOut(String port, String id, String ip) {
		this.ip = ip;
		sigue=true;
		gson = new Gson();
		this.port = port;
		this.id = id;
		CLIENT_ID = "KMO: " + id;
		producer = createProducer();
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
	 * Envia un mensaje al topico de SupervisorOutput
	 */
	public void send(MensajeControl msg) {
		String jsonString = gson.toJson(msg);
		ProducerRecord<String, String>  record= new ProducerRecord<String, String>(TOPIC_NAME, jsonString);
		producer.send(record);
		System.out.println("KafkaOut: Sent: " + jsonString);
		producer.flush();


	}


	public void close() {
		producer.close();
	}
	
}
