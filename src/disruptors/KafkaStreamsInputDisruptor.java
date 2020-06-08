package disruptors;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import com.google.gson.Gson;

import VOs.MensajeControl;
import main.Main;

public class KafkaStreamsInputDisruptor {

	private KafkaStreams stream;

	public KafkaStreamsInputDisruptor(String port, String ip, Buffer buffer, Main prin) {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "SpeedKafkaInputMain" + Main.SERVER_ID);
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ip + ":" + port);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		//System.out.println("se crea el KSID");
//		StreamsBuilder builder = new StreamsBuilder();
		
//		KStream<String, String> k = builder.stream("testChannel");
//		k.foreach(new ForeachAction<String, String>() {
//		    public void apply(String key, String value) {
//		        System.out.println(key + ": " + value);
//		    }
//		 });
		//System.out.println("Buffer" + buffer);
		Topology tp = new Topology();
		tp.addSource("Source", "testChannel")
			.addProcessor("addToBuffer", () -> new InputProcessor(buffer), "Source");
		tp.addSource("Source2", "SupervisorOutChannel")
			.addProcessor("controlMessage", () -> new InputProcessor2(prin), "Source2");
		
		
//		KTable<String, String> a;
		
		//stream = new KafkaStreams(builder.build(), props);
		stream = new KafkaStreams(tp, props);
		//System.out.println(tp.describe());
		//stream.start();
		
	}

	public void start() {
		System.out.println("Start KSID");
		stream.start();
		System.out.println("KSID: " + stream.state());
	}

	public void end() {
		System.out.println("KSID: Stop");
		stream.close();
	}
	
	public class InputProcessor implements Processor<String, String>{

		private Buffer buffer;
		
		public InputProcessor(Buffer buffer) {
			this.buffer = buffer;
		}
		
		@Override
		public void init(ProcessorContext context) {
			// TODO Auto-generated method stub
			System.out.println("KSID: INIT");
		}

		@Override
		public void process(String key, String value) {
			//System.out.println("KSID: " + key + ":" + value);
			//System.out.println(buffer);
			buffer.addMessage(value);
			
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class InputProcessor2 implements Processor<String, String>{

		private Main principal;
		
		private Gson gson;
		
		public InputProcessor2(Main prin) {
			this.principal = prin;
			gson = new Gson();
		}
		
		@Override
		public void init(ProcessorContext context) {
			// TODO Auto-generated method stub
			System.out.println("KSCI: INIT");
		}

		@Override
		public void process(String key, String value) {
			System.out.println("MensajeControl: " + key + ":" + value);
			//System.out.println(buffer);
			MensajeControl msg = gson.fromJson(value, MensajeControl.class);
			principal.manejarMensajeControl(msg);
			
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
		
	}


	
	

}
