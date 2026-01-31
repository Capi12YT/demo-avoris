package demo.avoris.infrastructure.adapter.in.web.handler;

import demo.avoris.domain.exception.InvalidCheckIn;
import demo.avoris.infrastructure.adapter.in.web.dto.ErrorResponseDTO;
import demo.avoris.infrastructure.adapter.out.mongo.exeption.SearchNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.logging.Logger;

@RestControllerAdvice
public class SearchExceptionHandler {

    private static final Logger log = Logger.getLogger(SearchExceptionHandler.class.getName());

    @ExceptionHandler(InvalidCheckIn.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCheckIn(
            InvalidCheckIn ex, HttpServletRequest request) {

        log.warning("Invalid check-in date validation error: " + ex.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "The check-in date provided is invalid." + ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SearchNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleSearchNotFound(
            SearchNotFoundException ex, HttpServletRequest request) {

        log.warning("Search not found error: " + ex.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "The requested search could not be found in our system. Please verify the search ID is correct or create a new search. " + ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.warning("Validation error: " + ex.getMessage());

        StringBuilder message = new StringBuilder("Validation failed for the request. Please check the following fields: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
            message.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ")
        );

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                message.toString(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        log.warning("Constraint violation error: " + ex.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Request validation failed. Please check your input parameters and ensure they meet the required constraints. " + ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.severe("Unexpected error occurred: " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred while processing your request. Please try again later or contact support if the problem persists.",
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
