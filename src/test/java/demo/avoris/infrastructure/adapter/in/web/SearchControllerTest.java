package demo.avoris.infrastructure.adapter.in.web;

import demo.avoris.TestDataBuilder;
import demo.avoris.application.dto.SearchDetailResponseDTO;
import demo.avoris.application.dto.SearchRequestDTO;
import demo.avoris.application.dto.SearchResponseDTO;
import demo.avoris.application.port.in.SearchUseCase;
import demo.avoris.domain.exception.InvalidCheckIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchUseCase searchUseCase;

    private SearchController searchController;

    @BeforeEach
    void setUp() {
        searchController = new SearchController(searchUseCase);
    }

    @Test
    void shouldGetSearchDetailSuccessfully_WhenValidSearchIdProvided() {
        // Given
        String searchId = TestDataBuilder.TEST_SEARCH_ID;
        SearchDetailResponseDTO expectedResponse = TestDataBuilder.createTestSearchDetailResponseDTO();
        when(searchUseCase.getHotelCount(searchId)).thenReturn(expectedResponse);

        // When
        ResponseEntity<SearchDetailResponseDTO> result = searchController.getSearchDetail(searchId);

        // Then
        assertAll("Get search detail success verification",
                () -> assertNotNull(result,
                        "La respuesta no debe ser null"),
                () -> assertEquals(HttpStatus.OK, result.getStatusCode(),
                        "El status debe ser 200 OK"),
                () -> assertNotNull(result.getBody(),
                        "El body de la respuesta no debe ser null"),
                () -> assertEquals(expectedResponse.searchId(), result.getBody().searchId(),
                        "El searchId debe coincidir"),
                () -> assertEquals(expectedResponse.count(), result.getBody().count(),
                        "El count debe coincidir"),
                () -> assertEquals(expectedResponse.search().hotelId(), result.getBody().search().hotelId(),
                        "El hotelId debe coincidir"),
                () -> verify(searchUseCase, times(1)).getHotelCount(searchId)
        );
    }

    @Test
    void shouldPropagateUseCaseException_WhenGetSearchDetailFails() {
        // Given
        String searchId = "non-existent-id";
        RuntimeException useCaseException = new RuntimeException("Search not found");
        when(searchUseCase.getHotelCount(searchId)).thenThrow(useCaseException);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> searchController.getSearchDetail(searchId),
                "Debe propagar la excepción del use case");

        assertAll("Use case exception propagation verification",
                () -> assertEquals("Search not found", exception.getMessage(),
                        "El mensaje de error debe ser propagado"),
                () -> verify(searchUseCase, times(1)).getHotelCount(searchId)
        );
    }

    @Test
    void shouldCreateSearchSuccessfully_WhenValidRequestProvided() {
        // Given
        SearchRequestDTO request = TestDataBuilder.createTestSearchRequestDTO();
        SearchResponseDTO expectedResponse = TestDataBuilder.createTestSearchResponseDTO();
        when(searchUseCase.createSearch(request)).thenReturn(expectedResponse);

        // When
        ResponseEntity<SearchResponseDTO> result = searchController.createSearch(request);

        // Then
        assertAll("Create search success verification",
                () -> assertNotNull(result,
                        "La respuesta no debe ser null"),
                () -> assertEquals(HttpStatus.OK, result.getStatusCode(),
                        "El status debe ser 200 OK"),
                () -> assertNotNull(result.getBody(),
                        "El body de la respuesta no debe ser null"),
                () -> assertEquals(expectedResponse.searchId(), result.getBody().searchId(),
                        "El searchId debe coincidir"),
                () -> verify(searchUseCase, times(1)).createSearch(request)
        );
    }

    @Test
    void shouldPropagateInvalidCheckInException_WhenInvalidDatesProvided() {
        // Given
        SearchRequestDTO request = TestDataBuilder.createTestSearchRequestDTOWithDates(
                TestDataBuilder.InvalidDates.INVALID_CHECK_IN,
                TestDataBuilder.InvalidDates.INVALID_CHECK_OUT
        );
        InvalidCheckIn invalidCheckInException = new InvalidCheckIn("Check-out date must be after check-in date.");
        when(searchUseCase.createSearch(request)).thenThrow(invalidCheckInException);

        // When & Then
        InvalidCheckIn exception = assertThrows(InvalidCheckIn.class,
                () -> searchController.createSearch(request),
                "Debe propagar la excepción InvalidCheckIn del use case");

        assertAll("InvalidCheckIn exception propagation verification",
                () -> assertEquals("Check-out date must be after check-in date.", exception.getMessage(),
                        "El mensaje de error debe ser propagado"),
                () -> verify(searchUseCase, times(1)).createSearch(request)
        );
    }


    @Test
    void shouldHandleMultipleGetRequestsCorrectly_WhenCalledWithDifferentIds() {
        // Given
        String searchId1 = "search-1";
        String searchId2 = "search-2";
        SearchDetailResponseDTO response1 = new SearchDetailResponseDTO(searchId1, TestDataBuilder.createTestSearchDTO(), 1);
        SearchDetailResponseDTO response2 = new SearchDetailResponseDTO(searchId2, TestDataBuilder.createTestSearchDTO(), 2);

        when(searchUseCase.getHotelCount(searchId1)).thenReturn(response1);
        when(searchUseCase.getHotelCount(searchId2)).thenReturn(response2);

        // When
        ResponseEntity<SearchDetailResponseDTO> result1 = searchController.getSearchDetail(searchId1);
        ResponseEntity<SearchDetailResponseDTO> result2 = searchController.getSearchDetail(searchId2);

        // Then
        assertAll("Multiple get requests handling verification",
                () -> assertEquals(HttpStatus.OK, result1.getStatusCode(),
                        "El primer get debe retornar 200 OK"),
                () -> assertEquals(HttpStatus.OK, result2.getStatusCode(),
                        "El segundo get debe retornar 200 OK"),
                () -> assertEquals(searchId1, result1.getBody().searchId(),
                        "El primer searchId debe coincidir"),
                () -> assertEquals(searchId2, result2.getBody().searchId(),
                        "El segundo searchId debe coincidir"),
                () -> assertEquals(1, result1.getBody().count(),
                        "El primer count debe coincidir"),
                () -> assertEquals(2, result2.getBody().count(),
                        "El segundo count debe coincidir"),
                () -> verify(searchUseCase, times(1)).getHotelCount(searchId1),
                () -> verify(searchUseCase, times(1)).getHotelCount(searchId2)
        );
    }
}
