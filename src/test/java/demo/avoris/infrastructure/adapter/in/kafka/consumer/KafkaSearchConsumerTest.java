package demo.avoris.infrastructure.adapter.in.kafka.consumer;

import demo.avoris.TestDataBuilder;
import demo.avoris.application.port.in.SearchUseCase;
import demo.avoris.domain.model.Search;
import demo.avoris.infrastructure.adapter.in.kafka.exeption.ErrorConsumeTopic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaSearchConsumer Tests")
class KafkaSearchConsumerTest {

    @Mock
    private SearchUseCase useCase;

    @Mock
    private ObjectMapper objectMapper;

    private KafkaSearchConsumer kafkaSearchConsumer;

    @BeforeEach
    void setUp() {
        kafkaSearchConsumer = new KafkaSearchConsumer(useCase, objectMapper);
    }

    @Test
    @DisplayName("Debería procesar mensaje Kafka exitosamente cuando JSON es válido")
    void shouldProcessKafkaMessageSuccessfully_WhenValidJsonProvided() throws Exception {
        // Given
        String jsonMessage = "{\"searchId\":\"test-123\",\"hotelId\":\"hotel-456\"}";
        Search search = TestDataBuilder.createTestSearch();
        Search savedSearch = TestDataBuilder.createTestSearchWithCount(5);

        when(objectMapper.readValue(jsonMessage, Search.class)).thenReturn(search);
        when(useCase.saveSearch(search)).thenReturn(savedSearch);

        // When & Then
        assertAll("Kafka message processing success verification",
                () -> assertDoesNotThrow(() -> kafkaSearchConsumer.listen(jsonMessage),
                        "No debe lanzar excepción con JSON válido"),
                () -> verify(objectMapper, times(1)).readValue(jsonMessage, Search.class),
                () -> verify(useCase, times(1)).saveSearch(search)
        );
    }

    @Test
    @DisplayName("Debería lanzar ErrorConsumeTopic cuando ObjectMapper falla")
    void shouldThrowErrorConsumeTopic_WhenObjectMapperFails() throws Exception {
        // Given
        String invalidJsonMessage = "invalid-json";
        Exception mappingException = new RuntimeException("JSON parsing error");

        when(objectMapper.readValue(invalidJsonMessage, Search.class)).thenThrow(mappingException);

        // When & Then
        ErrorConsumeTopic exception = assertThrows(ErrorConsumeTopic.class,
                () -> kafkaSearchConsumer.listen(invalidJsonMessage),
                "Debe lanzar ErrorConsumeTopic cuando falla el parsing JSON");

        assertAll("ObjectMapper error handling verification",
                () -> assertTrue(exception.getMessage().contains("Error processing message:"),
                        "El mensaje debe contener el prefijo de error"),
                () -> assertTrue(exception.getMessage().contains("JSON parsing error"),
                        "El mensaje debe contener la causa del error"),
                () -> verify(objectMapper, times(1)).readValue(invalidJsonMessage, Search.class),
                () -> verifyNoInteractions(useCase)
        );
    }

    @Test
    @DisplayName("Debería lanzar ErrorConsumeTopic cuando UseCase falla")
    void shouldThrowErrorConsumeTopic_WhenUseCaseFails() throws Exception {
        // Given
        String jsonMessage = "{\"searchId\":\"test-123\",\"hotelId\":\"hotel-456\"}";
        Search search = TestDataBuilder.createTestSearch();
        RuntimeException useCaseException = new RuntimeException("Database error");

        when(objectMapper.readValue(jsonMessage, Search.class)).thenReturn(search);
        when(useCase.saveSearch(search)).thenThrow(useCaseException);

        // When & Then
        ErrorConsumeTopic exception = assertThrows(ErrorConsumeTopic.class,
                () -> kafkaSearchConsumer.listen(jsonMessage),
                "Debe lanzar ErrorConsumeTopic cuando falla el use case");

        assertAll("UseCase error handling verification",
                () -> assertTrue(exception.getMessage().contains("Error processing message:"),
                        "El mensaje debe contener el prefijo de error"),
                () -> assertTrue(exception.getMessage().contains("Database error"),
                        "El mensaje debe contener la causa del error"),
                () -> verify(objectMapper, times(1)).readValue(jsonMessage, Search.class),
                () -> verify(useCase, times(1)).saveSearch(search)
        );
    }

    @Test
    @DisplayName("Debería manejar múltiples mensajes Kafka correctamente")
    void shouldHandleMultipleKafkaMessages_WhenCalledMultipleTimes() throws Exception {
        // Given
        String jsonMessage1 = "{\"searchId\":\"test-123\"}";
        String jsonMessage2 = "{\"searchId\":\"test-456\"}";
        Search search1 = TestDataBuilder.createTestSearchWithId("test-123");
        Search search2 = TestDataBuilder.createTestSearchWithId("test-456");
        Search savedSearch1 = TestDataBuilder.createTestSearchWithCount(1);
        Search savedSearch2 = TestDataBuilder.createTestSearchWithCount(2);

        when(objectMapper.readValue(jsonMessage1, Search.class)).thenReturn(search1);
        when(objectMapper.readValue(jsonMessage2, Search.class)).thenReturn(search2);
        when(useCase.saveSearch(search1)).thenReturn(savedSearch1);
        when(useCase.saveSearch(search2)).thenReturn(savedSearch2);

        // When & Then
        assertAll("Multiple Kafka messages handling verification",
                () -> assertDoesNotThrow(() -> kafkaSearchConsumer.listen(jsonMessage1),
                        "No debe lanzar excepción con el primer mensaje"),
                () -> assertDoesNotThrow(() -> kafkaSearchConsumer.listen(jsonMessage2),
                        "No debe lanzar excepción con el segundo mensaje"),
                () -> verify(objectMapper, times(1)).readValue(jsonMessage1, Search.class),
                () -> verify(objectMapper, times(1)).readValue(jsonMessage2, Search.class),
                () -> verify(useCase, times(1)).saveSearch(search1),
                () -> verify(useCase, times(1)).saveSearch(search2)
        );
    }

    @Test
    @DisplayName("Debería procesar mensaje con JSON complejo correctamente")
    void shouldProcessComplexJsonMessage_WhenCompleteSearchDataProvided() throws Exception {
        // Given
        String complexJsonMessage = "{\"searchId\":\"complex-123\",\"hotelId\":\"hotel-789\",\"checkIn\":\"2024-12-25\",\"checkOut\":\"2024-12-30\",\"ages\":[30,25,5],\"count\":3}";
        Search complexSearch = TestDataBuilder.createTestSearchWithCount(3);
        Search savedComplexSearch = TestDataBuilder.createTestSearchWithCount(3);

        when(objectMapper.readValue(complexJsonMessage, Search.class)).thenReturn(complexSearch);
        when(useCase.saveSearch(complexSearch)).thenReturn(savedComplexSearch);

        // When & Then
        assertAll("Complex JSON message processing verification",
                () -> assertDoesNotThrow(() -> kafkaSearchConsumer.listen(complexJsonMessage),
                        "No debe lanzar excepción con JSON complejo"),
                () -> verify(objectMapper, times(1)).readValue(complexJsonMessage, Search.class),
                () -> verify(useCase, times(1)).saveSearch(complexSearch)
        );
    }

    @Test
    @DisplayName("Debería manejar mensajes vacíos correctamente")
    void shouldHandleEmptyMessage_WhenEmptyStringProvided() throws Exception {
        // Given
        String emptyMessage = "";
        Exception mappingException = new RuntimeException("Empty message error");

        when(objectMapper.readValue(emptyMessage, Search.class)).thenThrow(mappingException);

        // When & Then
        ErrorConsumeTopic exception = assertThrows(ErrorConsumeTopic.class,
                () -> kafkaSearchConsumer.listen(emptyMessage),
                "Debe lanzar ErrorConsumeTopic con mensaje vacío");

        assertAll("Empty message handling verification",
                () -> assertTrue(exception.getMessage().contains("Error processing message:"),
                        "El mensaje debe contener el prefijo de error"),
                () -> verify(objectMapper, times(1)).readValue(emptyMessage, Search.class),
                () -> verifyNoInteractions(useCase)
        );
    }

    @Test
    @DisplayName("Debería propagar excepción original en el mensaje de error")
    void shouldPropagateOriginalExceptionInErrorMessage_WhenAnyExceptionOccurs() throws Exception {
        // Given
        String jsonMessage = "{\"searchId\":\"test\"}";
        String originalErrorMessage = "Specific database connection error";
        Exception originalException = new RuntimeException(originalErrorMessage);

        when(objectMapper.readValue(jsonMessage, Search.class)).thenThrow(originalException);

        // When & Then
        ErrorConsumeTopic exception = assertThrows(ErrorConsumeTopic.class,
                () -> kafkaSearchConsumer.listen(jsonMessage));

        assertAll("Original exception propagation verification",
                () -> assertTrue(exception.getMessage().contains("Error processing message:"),
                        "Debe contener el prefijo del mensaje de error"),
                () -> assertTrue(exception.getMessage().contains(originalErrorMessage),
                        "Debe contener el mensaje de la excepción original"),
                () -> verify(objectMapper, times(1)).readValue(jsonMessage, Search.class)
        );
    }
}
