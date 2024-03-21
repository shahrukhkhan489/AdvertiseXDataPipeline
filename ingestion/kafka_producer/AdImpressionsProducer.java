import org.apache.kafka.clients.producer.*;
import org.apache.log4j.Logger;
import java.util.Properties;

public class AdImpressionsProducer {
    private static final Logger logger = Logger.getLogger(AdImpressionsProducer.class);

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        try {
            for (int i = 0; i < 100; i++) {
                String impression = "{\"impression_id\":\"" + i + "\",\"ad_creative_id\":\"creative_" + i + "\",\"user_id\":\"user_" + i + "\",\"timestamp\":\"2021-01-01T12:00:00Z\",\"website\":\"example.com\"}";
                producer.send(new ProducerRecord<>("ad_impressions_topic", Integer.toString(i), impression), new Callback() {
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
