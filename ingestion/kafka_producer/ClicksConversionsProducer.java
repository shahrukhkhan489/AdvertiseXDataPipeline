import org.apache.kafka.clients.producer.*;
import org.apache.log4j.Logger;
import java.util.Properties;

public class ClicksConversionsProducer {
    private static final Logger logger = Logger.getLogger(ClicksConversionsProducer.class);

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        try {
            for (int i = 0; i < 100; i++) {
                String eventType = (i % 2 == 0) ? "click" : "conversion";
                String csvRecord = "2021-01-01T12:00:00Z,user_" + i + ",campaign_" + i + "," + eventType;
                producer.send(new ProducerRecord<>("clicks_conversions_topic", Integer.toString(i), csvRecord), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        if (e != null) {
                            logger.error("Error sending message to Kafka: ", e);
                        }
                    }
                });
            }
        } finally {
            producer.close();
        }
    }
}
