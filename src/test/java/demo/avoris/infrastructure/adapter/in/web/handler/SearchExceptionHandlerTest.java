package demo.avoris.infrastructure.adapter.in.web.handler;

import demo.avoris.domain.exception.InvalidCheckIn;
import demo.avoris.infrastructure.adapter.in.web.dto.ErrorResponseDTO;
import demo.avoris.infrastructure.adapter.out.mongo.exeption.SearchNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private SearchExceptionHandler searchExceptionHandler;

    private static final String TEST_PATH = "/demo-avoris/search";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(TEST_PATH);
    }

    @Test
    void handleInvalidCheckIn_ShouldReturnBadRequest() {
        // Given
        String errorMessage = "Check-in date cannot be in the past";
        InvalidCheckIn exception = new InvalidCheckIn(errorMessage);

        // When
        ResponseEntity<ErrorResponseDTO> response = searchExceptionHandler.handleInvalidCheckIn(exception, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.status());
        assertEquals("Bad Request", errorResponse.error());
        assertTrue(errorResponse.message().contains("The check-in date provided is invalid"));
        assertTrue(errorResponse.message().contains(errorMessage));
        assertEquals(TEST_PATH, errorResponse.path());
        assertNotNull(errorResponse.timestamp());
    }

    @Test
    void handleSearchNotFound_ShouldReturnNotFound() {
        // Given
        String errorMessage = "Search with ID 123 not found";
        SearchNotFoundException exception = new SearchNotFoundException(errorMessage);

        // When
        ResponseEntity<ErrorResponseDTO> response = searchExceptionHandler.handleSearchNotFound(exception, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.status());
        assertEquals("Not Found", errorResponse.error());
        assertTrue(errorResponse.message().contains("The requested search could not be found"));
        assertTrue(errorResponse.message().contains(errorMessage));
        assertEquals(TEST_PATH, errorResponse.path());
        assertNotNull(errorResponse.timestamp());
    }

    @Test
    void handleValidationErrors_ShouldReturnBadRequestWithFieldDetails() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        List<FieldError> fieldErrors = List.of(
            new FieldError("searchRequest", "checkInDate", "must not be null"),
            new FieldError("searchRequest", "destination", "must not be empty")
        );
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // When
        ResponseEntity<ErrorResponseDTO> response = searchExceptionHandler.handleValidationErrors(exception, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.status());
        assertEquals("Validation Failed", errorResponse.error());
        assertTrue(errorResponse.message().contains("Validation failed for the request"));
        assertTrue(errorResponse.message().contains("checkInDate - must not be null"));
        assertTrue(errorResponse.message().contains("destination - must not be empty"));
        assertEquals(TEST_PATH, errorResponse.path());
        assertNotNull(errorResponse.timestamp());
    }

    @Test
    void handleConstraintViolation_ShouldReturnBadRequest() {
        // Given
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", violations);

        // When
        ResponseEntity<ErrorResponseDTO> response = searchExceptionHandler.handleConstraintViolation(exception, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.status());
        assertEquals("Validation Failed", errorResponse.error());
        assertTrue(errorResponse.message().contains("Request validation failed"));
        assertTrue(errorResponse.message().contains("Constraint violation"));
        assertEquals(TEST_PATH, errorResponse.path());
        assertNotNull(errorResponse.timestamp());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Given
        RuntimeException exception = new RuntimeException("Unexpected database error");

        // When
        ResponseEntity<ErrorResponseDTO> response = searchExceptionHandler.handleGenericException(exception, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.status());
        assertEquals("Internal Server Error", errorResponse.error());
        assertTrue(errorResponse.message().contains("An unexpected error occurred"));
        assertFalse(errorResponse.message().contains("Unexpected database error")); // No debe exponer detalles internos
        assertEquals(TEST_PATH, errorResponse.path());
        assertNotNull(errorResponse.timestamp());
    }

    @Test
    void handleInvalidCheckIn_WithNullMessage_ShouldHandleGracefully() {
        // Given
        InvalidCheckIn exception = new InvalidCheckIn(null);

        // When
        ResponseEntity<ErrorResponseDTO> response = searchExceptionHandler.handleInvalidCheckIn(exception, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertTrue(errorResponse.message().contains("The check-in date provided is invalid"));
    }

    @Test
    void handleSearchNotFound_WithEmptyMessage_ShouldHandleGracefully() {
        // Given
        SearchNotFoundException exception = new SearchNotFoundException("");

        // When
        ResponseEntity<ErrorResponseDTO> response = searchExceptionHandler.handleSearchNotFound(exception, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertTrue(errorResponse.message().contains("The requested search could not be found"));
    }

    @Test
    void handleValidationErrors_WithEmptyFieldErrors_ShouldReturnGenericMessage() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // When
        ResponseEntity<ErrorResponseDTO> response = searchExceptionHandler.handleValidationErrors(exception, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Validation Failed", errorResponse.error());
        assertTrue(errorResponse.message().contains("Validation failed for the request"));
    }
}
