package disruptors;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.google.gson.Gson;

import VOs.Response;




public class KafkaOutputDisruptor extends Thread{

	public OutBuffer buffer;
	public boolean sigue;


	public static String KAFKA_BROKERS = "172.24.41.149:8081";

	public static String CLIENT_ID="client1";

	public static String TOPIC_NAME="testChannel2";

	public static String GROUP_ID_CONFIG="consumerGroup1";

	public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;

	public static String OFFSET_RESET_LATEST="latest";

	public static String OFFSET_RESET_EARLIER="earliest";

	public static Integer MAX_POLL_RECORDS=1;

	private Gson gson;

	public KafkaOutputDisruptor(OutBuffer buffer) {
		this.buffer=buffer;
		sigue=true;
		gson = new Gson();
	}

	public static Producer<Long, String> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				KAFKA_BROKERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
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
			if(os==null) {
				yield();
			}
			else {
				Response rs= os.getResponse();
				os.setResponse(null);
				String jsonString = gson.toJson(rs);
				ProducerRecord<Long, String>  record= new ProducerRecord<Long, String>(TOPIC_NAME, jsonString);
				producer.send(record);
				try {
					RecordMetadata metadata = producer.send(record).get();
					//System.out.println("Record enviado con: "+rs.toString());
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error enviando record: "+ e);
				}

			}
		}
		producer.close();



	}

	/**
	 * termina el while infinito
	 */
	public void end() {
		sigue = false;
	}

}
