package demo.avoris.infrastructure.adapter.out.kafka.producer;

import demo.avoris.TestDataBuilder;
import demo.avoris.domain.model.Search;
import demo.avoris.infrastructure.adapter.out.kafka.exeption.ErrorSendTopic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaSearchProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private KafkaSearchProducer kafkaSearchProducer;

    private static final String TEST_TOPIC_NAME = "test-hotel-searches";

    @BeforeEach
    void setUp() {
        kafkaSearchProducer = new KafkaSearchProducer(kafkaTemplate, objectMapper);
        // Inyectar el valor del tópico usando ReflectionTestUtils
        ReflectionTestUtils.setField(kafkaSearchProducer, "topicName", TEST_TOPIC_NAME);
    }

    @Test
    void shouldHaveCorrectTopicProperty_WhenPropertyIsInjected() {
        // When
        String actualTopicName = (String) ReflectionTestUtils.getField(kafkaSearchProducer, "topicName");

        // Then
        assertAll("Topic property injection verification",
                () -> assertEquals(TEST_TOPIC_NAME, actualTopicName,
                        "El tópico debe coincidir con el valor inyectado desde properties"),
                () -> assertNotNull(actualTopicName, "El tópico no debe ser null")
        );
    }

    @Test
    void shouldPublishSearchSuccessfully_WhenValidSearchProvided() throws Exception {
        // Given
        Search search = TestDataBuilder.createTestSearch();
        String expectedPayload = "{\"searchId\":\"test-search-123\",\"hotelId\":\"hotel-456\"}";

        when(objectMapper.writeValueAsString(search)).thenReturn(expectedPayload);
        when(kafkaTemplate.send(any(String.class), any(String.class), any(String.class))).thenReturn(null);

        // When & Then
        assertAll("Kafka search publishing success verification",
                () -> assertDoesNotThrow(() -> kafkaSearchProducer.publishSearch(search),
                        "No debe lanzar excepción con search válido"),
                () -> verify(objectMapper, times(1)).writeValueAsString(search),
                () -> verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC_NAME), eq(search.searchId()), eq(expectedPayload))
        );
    }

    @Test
    void shouldThrowErrorSendTopic_WhenObjectMapperFails() throws Exception {
        // Given
        Search search = TestDataBuilder.createTestSearch();
        Exception mappingException = new RuntimeException("JSON serialization error");

        when(objectMapper.writeValueAsString(search)).thenThrow(mappingException);

        // When & Then
        ErrorSendTopic exception = assertThrows(ErrorSendTopic.class,
                () -> kafkaSearchProducer.publishSearch(search),
                "Debe lanzar ErrorSendTopic cuando falla la serialización JSON");

        assertAll("ObjectMapper error handling verification",
                () -> assertTrue(exception.getMessage().contains("Error processing message:"),
                        "El mensaje debe contener el prefijo de error"),
                () -> assertTrue(exception.getMessage().contains("JSON serialization error"),
                        "El mensaje debe contener la causa del error"),
                () -> verify(objectMapper, times(1)).writeValueAsString(search),
                () -> verifyNoInteractions(kafkaTemplate)
        );
    }

    @Test
    void shouldThrowErrorSendTopic_WhenKafkaTemplateFails() throws Exception {
        // Given
        Search search = TestDataBuilder.createTestSearch();
        String payload = "{\"searchId\":\"test-search-123\"}";
        RuntimeException kafkaException = new RuntimeException("Kafka connection error");

        when(objectMapper.writeValueAsString(search)).thenReturn(payload);
        when(kafkaTemplate.send(any(String.class), any(String.class), any(String.class))).thenThrow(kafkaException);

        // When & Then
        ErrorSendTopic exception = assertThrows(ErrorSendTopic.class,
                () -> kafkaSearchProducer.publishSearch(search),
                "Debe lanzar ErrorSendTopic cuando falla el envío a Kafka");

        assertAll("KafkaTemplate error handling verification",
                () -> assertTrue(exception.getMessage().contains("Error processing message:"),
                        "El mensaje debe contener el prefijo de error"),
                () -> assertTrue(exception.getMessage().contains("Kafka connection error"),
                        "El mensaje debe contener la causa del error"),
                () -> verify(objectMapper, times(1)).writeValueAsString(search),
                () -> verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC_NAME), eq(search.searchId()), eq(payload))
        );
    }

    @Test
    void shouldUseCorrectTopic_WhenPublishingSearch() throws Exception {
        // Given
        Search search = TestDataBuilder.createTestSearch();
        String payload = "{\"searchId\":\"test-search-123\"}";

        when(objectMapper.writeValueAsString(search)).thenReturn(payload);
        when(kafkaTemplate.send(any(String.class), any(String.class), any(String.class))).thenReturn(null);

        // When
        kafkaSearchProducer.publishSearch(search);

        // Then
        assertAll("Kafka topic verification",
                () -> verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC_NAME), any(String.class), any(String.class)),
                () -> verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC_NAME), eq(search.searchId()), eq(payload))
        );
    }

    @Test
    void shouldUseSearchIdAsMessageKey_WhenPublishingSearch() throws Exception {
        // Given
        String customSearchId = "custom-search-789";
        Search search = TestDataBuilder.createTestSearchWithId(customSearchId);
        String payload = "{\"searchId\":\"custom-search-789\"}";

        when(objectMapper.writeValueAsString(search)).thenReturn(payload);
        when(kafkaTemplate.send(any(String.class), any(String.class), any(String.class))).thenReturn(null);

        // When
        kafkaSearchProducer.publishSearch(search);

        // Then
        assertAll("Kafka message key verification",
                () -> verify(kafkaTemplate, times(1)).send(any(String.class), eq(customSearchId), any(String.class)),
                () -> verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC_NAME), eq(customSearchId), eq(payload))
        );
    }

    @Test
    void shouldHandleMultiplePublications_WhenCalledMultipleTimes() throws Exception {
        // Given
        Search search1 = TestDataBuilder.createTestSearchWithId("search-1");
        Search search2 = TestDataBuilder.createTestSearchWithId("search-2");
        String payload1 = "{\"searchId\":\"search-1\"}";
        String payload2 = "{\"searchId\":\"search-2\"}";

        when(objectMapper.writeValueAsString(search1)).thenReturn(payload1);
        when(objectMapper.writeValueAsString(search2)).thenReturn(payload2);
        when(kafkaTemplate.send(any(String.class), any(String.class), any(String.class))).thenReturn(null);

        // When & Then
        assertAll("Multiple publications handling verification",
                () -> assertDoesNotThrow(() -> kafkaSearchProducer.publishSearch(search1),
                        "No debe lanzar excepción con la primera publicación"),
                () -> assertDoesNotThrow(() -> kafkaSearchProducer.publishSearch(search2),
                        "No debe lanzar excepción con la segunda publicación"),
                () -> verify(objectMapper, times(1)).writeValueAsString(search1),
                () -> verify(objectMapper, times(1)).writeValueAsString(search2),
                () -> verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC_NAME), eq("search-1"), eq(payload1)),
                () -> verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC_NAME), eq("search-2"), eq(payload2))
        );
    }

    @Test
    void shouldSerializeComplexSearch_WhenCompleteSearchDataProvided() throws Exception {
        // Given
        Search complexSearch = TestDataBuilder.createTestSearchWithCount(5);
        String complexPayload = "{\"searchId\":\"test-search-123\",\"hotelId\":\"hotel-456\",\"checkIn\":\"2024-12-25\",\"checkOut\":\"2024-12-30\",\"ages\":[30,25,5,3],\"count\":5}";

        when(objectMapper.writeValueAsString(complexSearch)).thenReturn(complexPayload);
        when(kafkaTemplate.send(any(String.class), any(String.class), any(String.class))).thenReturn(null);

        // When & Then
        assertAll("Complex search serialization verification",
                () -> assertDoesNotThrow(() -> kafkaSearchProducer.publishSearch(complexSearch),
                        "No debe lanzar excepción con búsqueda compleja"),
                () -> verify(objectMapper, times(1)).writeValueAsString(complexSearch),
                () -> verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC_NAME), eq(complexSearch.searchId()), eq(complexPayload))
        );
    }

    @Test
    void shouldPropagateOriginalExceptionInErrorMessage_WhenAnyExceptionOccurs() throws Exception {
        // Given
        Search search = TestDataBuilder.createTestSearch();
        String originalErrorMessage = "Specific Kafka broker unavailable";
        Exception originalException = new RuntimeException(originalErrorMessage);

        when(objectMapper.writeValueAsString(search)).thenThrow(originalException);

        // When & Then
        ErrorSendTopic exception = assertThrows(ErrorSendTopic.class,
                () -> kafkaSearchProducer.publishSearch(search));

        assertAll("Original exception propagation verification",
                () -> assertTrue(exception.getMessage().contains("Error processing message:"),
                        "Debe contener el prefijo del mensaje de error"),
                () -> assertTrue(exception.getMessage().contains(originalErrorMessage),
                        "Debe contener el mensaje de la excepción original"),
                () -> verify(objectMapper, times(1)).writeValueAsString(search)
        );
    }
}
