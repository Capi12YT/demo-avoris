package demo.avoris.infrastructure.adapter.in.kafka.consumer;

import demo.avoris.application.port.in.SearchUseCase;
import demo.avoris.domain.model.Search;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import demo.avoris.infrastructure.adapter.in.kafka.exeption.ErrorConsumeTopic;
import java.util.logging.Logger;


@Component
public class KafkaSearchConsumer {

    private final SearchUseCase useCase;
    private final ObjectMapper objectMapper;
    private final Logger log = Logger.getLogger(KafkaSearchConsumer.class.getName());

    public KafkaSearchConsumer(SearchUseCase useCase, ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "hotel_availability_searches",
            groupId = "hotel_availability_group"
    )
    public void listen(String message) {
        try {
            Search search = objectMapper.readValue(message, Search.class);
            Search searchSave = useCase.saveSearch(search);
            log.info("Search saved with id: " + searchSave.searchId());
        } catch (Exception e) {
            throw new ErrorConsumeTopic("Error processing message: " + e.getMessage());
        }
    }
}

