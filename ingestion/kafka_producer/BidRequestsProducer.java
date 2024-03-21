import org.apache.kafka.clients.producer.*;
import org.apache.log4j.Logger;
import java.util.Properties;

public class BidRequestsProducer {
    private static final Logger logger = Logger.getLogger(BidRequestsProducer.class);

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        try {
            for (int i = 0; i < 100; i++) {
                String bidRequest = "{\"request_id\":\"" + i + "\",\"user_info\":{\"user_id\":\"user_" + i + "\",\"user_attributes\":{\"age\":25,\"gender\":\"female\"}},\"auction_details\":{\"auction_id\":\"auction_" + i + "\",\"bid_floor\":0.5},\"ad_targeting_criteria\":{\"location\":\"New York\",\"interests\":[\"technology\",\"advertising\"]}}";
                producer.send(new ProducerRecord<>("bid_requests_topic", Integer.toString(i), bidRequest), new Callback() {
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
