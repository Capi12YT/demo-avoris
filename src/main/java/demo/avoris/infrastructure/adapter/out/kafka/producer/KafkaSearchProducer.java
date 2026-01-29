package demo.avoris.infrastructure.adapter.out.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.avoris.application.port.out.SearchEventPublisherPort;
import demo.avoris.domain.model.Search;

import demo.avoris.infrastructure.adapter.out.kafka.exeption.ErrorSendTopic;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;


import java.util.logging.Logger;

@Repository
public class KafkaSearchProducer implements SearchEventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final Logger log = Logger.getLogger(KafkaSearchProducer.class.getName());


    public KafkaSearchProducer(
            KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishSearch(Search search) {
        try {
            String payload = objectMapper.writeValueAsString(search);
            String topic = "hotel_availability_searches";
            kafkaTemplate.send(topic, search.searchId(), payload);
            log.info("Published search with id: " + search.searchId());
        } catch (Exception e) {
            throw new ErrorSendTopic("Error processing message: " + e.getMessage());
        }
    }
}
