package disruptors;

import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;



public class KafkaInputDisruptor extends Thread {

	public static String KAFKA_BROKERS = "172.24.41.149:";

	public static String CLIENT_ID="client1";

	public static String TOPIC_NAME="testChannel";

	public static String GROUP_ID_CONFIG="consumerGroup1";

	public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;

	public static String OFFSET_RESET_LATEST="latest";

	public static String OFFSET_RESET_EARLIER="earliest";

	public static Integer MAX_POLL_RECORDS=1;

	private Buffer buffer;
	
	private boolean sigue;
	
	private String port;
	
	public KafkaInputDisruptor(Buffer buffer, String port) {
		this.buffer = buffer;
		sigue = true;
		this.port = port;
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
		ConsumerRecords<Long, String> records;
		while(sigue) {
			records = cons.poll(100);
			for(ConsumerRecord<Long, String> record: records) {
				String message = "KafkaIinput: " + record.value();
				//System.out.println(message);
				while(!buffer.addMessage(message)) {
					try {
						//System.out.println("duerme logic al outputtear");
						sleep(10L);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}
		cons.close();
		System.out.println("KID: " + port + " closed");
	}
	
	public void end() {
		sigue = false;
	}

}
