package se.liu.ida.tdp024.account.util.logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLogger {

    private final boolean isTesting;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaLogger(boolean isTesting) {
        this.kafkaTemplate = null;
        this.isTesting = isTesting;
    }

    @Autowired
    public KafkaLogger(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.isTesting = false;
    }

    public void sendRestMessage(String msg) {
        if (!isTesting && kafkaTemplate != null) {
            kafkaTemplate.send("logs.http.requests", msg);
        }
    }

    //// Kafka Logger

    public void sendDataMessage(String msg) {
        if (!isTesting && kafkaTemplate != null) {
            kafkaTemplate.send("logs.data.transactions", msg);
        }
    }
}
