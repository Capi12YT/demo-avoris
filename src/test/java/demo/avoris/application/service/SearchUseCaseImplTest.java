package demo.avoris.application.service;

import demo.avoris.TestDataBuilder;
import demo.avoris.application.dto.SearchDetailResponseDTO;
import demo.avoris.application.dto.SearchRequestDTO;
import demo.avoris.application.dto.SearchResponseDTO;
import demo.avoris.application.port.out.SearchEventPublisherPort;
import demo.avoris.application.port.out.SearchRepositoryPort;
import demo.avoris.domain.exception.InvalidCheckIn;
import demo.avoris.domain.model.Search;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class SearchUseCaseImplTest {

    @Mock
    private SearchRepositoryPort repository;

    @Mock
    private SearchEventPublisherPort publisher;

    private SearchUseCaseImpl searchUseCase;

    @BeforeEach
    void setUp() {
        searchUseCase = new SearchUseCaseImpl(repository, publisher);
    }

    @Test
    void shouldCreateSearchSuccessfully_WhenValidDataProvided() {
        // Given
        SearchRequestDTO request = TestDataBuilder.createTestSearchRequestDTO();
        doNothing().when(publisher).publishSearch(any(Search.class));

        // When
        SearchResponseDTO result = searchUseCase.createSearch(request);

        // Then
        assertAll("Create search success verification",
                () -> assertNotNull(result,
                        "El resultado no debe ser null"),
                () -> assertNotNull(result.searchId(),
                        "El searchId no debe ser null"),
                () -> assertFalse(result.searchId().isEmpty(),
                        "El searchId no debe estar vacío"),
                () -> verify(publisher, times(1)).publishSearch(any(Search.class)),
                () -> verifyNoInteractions(repository)
        );
    }

    @Test
    void shouldThrowInvalidCheckIn_WhenCheckInIsAfterCheckOut() {
        // Given
        SearchRequestDTO request = TestDataBuilder.createTestSearchRequestDTOWithDates(
                TestDataBuilder.InvalidDates.INVALID_CHECK_IN,
                TestDataBuilder.InvalidDates.INVALID_CHECK_OUT
        );

        // When & Then
        InvalidCheckIn exception = assertThrows(InvalidCheckIn.class,
                () -> searchUseCase.createSearch(request),
                "Debe lanzar InvalidCheckIn cuando checkIn es después de checkOut");

        assertAll("InvalidCheckIn exception verification",
                () -> assertEquals("Check-out date must be after check-in date.", exception.getMessage(),
                        "El mensaje de error debe ser correcto"),
                () -> verifyNoInteractions(publisher),
                () -> verifyNoInteractions(repository)
        );
    }

    @Test
    void shouldThrowInvalidCheckIn_WhenCheckInEqualsCheckOut() {
        // Given
        SearchRequestDTO request = TestDataBuilder.createTestSearchRequestDTOWithDates(
                TestDataBuilder.EqualDates.SAME_DATE,
                TestDataBuilder.EqualDates.SAME_DATE
        );

        // When & Then
        InvalidCheckIn exception = assertThrows(InvalidCheckIn.class,
                () -> searchUseCase.createSearch(request),
                "Debe lanzar InvalidCheckIn cuando checkIn es igual a checkOut");

        assertAll("InvalidCheckIn equal dates exception verification",
                () -> assertEquals("Check-out date must be after check-in date.", exception.getMessage(),
                        "El mensaje de error debe ser correcto"),
                () -> verifyNoInteractions(publisher),
                () -> verifyNoInteractions(repository)
        );
    }

    @Test
    void shouldGetHotelCountSuccessfully_WhenSearchIdExists() {
        // Given
        String searchId = TestDataBuilder.TEST_SEARCH_ID;
        Search search = TestDataBuilder.createTestSearch();
        when(repository.findBySearchId(searchId)).thenReturn(search);

        // When
        SearchDetailResponseDTO result = searchUseCase.getHotelCount(searchId);

        // Then
        assertAll("Get hotel count success verification",
                () -> assertNotNull(result,
                        "El resultado no debe ser null"),
                () -> assertEquals(searchId, result.searchId(),
                        "El searchId debe coincidir"),
                () -> assertEquals(search.hotelId(), result.search().hotelId(),
                        "El hotelId debe coincidir"),
                () -> assertEquals(search.checkIn(), result.search().checkIn(),
                        "La fecha de checkIn debe coincidir"),
                () -> assertEquals(search.checkOut(), result.search().checkOut(),
                        "La fecha de checkOut debe coincidir"),
                () -> assertEquals(search.ages(), result.search().ages(),
                        "Las edades deben coincidir"),
                () -> assertEquals(search.count(), result.count(),
                        "El count debe coincidir"),
                () -> verify(repository, times(1)).findBySearchId(searchId),
                () -> verifyNoInteractions(publisher)
        );
    }

    @Test
    void shouldSaveSearchSuccessfully_WhenValidSearchProvided() {
        // Given
        Search search = TestDataBuilder.createTestSearch();
        Search savedSearch = TestDataBuilder.createTestSearchWithCount(5);
        when(repository.save(search)).thenReturn(savedSearch);

        // When
        Search result = searchUseCase.saveSearch(search);

        // Then
        assertAll("Save search success verification",
                () -> assertNotNull(result,
                        "El resultado no debe ser null"),
                () -> assertEquals(savedSearch.searchId(), result.searchId(),
                        "El searchId debe coincidir con el guardado"),
                () -> assertEquals(savedSearch.hotelId(), result.hotelId(),
                        "El hotelId debe coincidir con el guardado"),
                () -> assertEquals(savedSearch.count(), result.count(),
                        "El count debe coincidir con el guardado"),
                () -> verify(repository, times(1)).save(search),
                () -> verifyNoInteractions(publisher)
        );
    }

    @Test
    void shouldPropagateRepositoryException_WhenFindBySearchIdFails() {
        // Given
        String searchId = "non-existent-id";
        RuntimeException repositoryException = new RuntimeException("Repository error");
        when(repository.findBySearchId(searchId)).thenThrow(repositoryException);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> searchUseCase.getHotelCount(searchId),
                "Debe propagar la excepción del repositorio");

        assertAll("Repository exception propagation verification",
                () -> assertEquals("Repository error", exception.getMessage(),
                        "El mensaje de error debe ser propagado"),
                () -> verify(repository, times(1)).findBySearchId(searchId),
                () -> verifyNoInteractions(publisher)
        );
    }

    @Test
    void shouldPropagatePublisherException_WhenPublishSearchFails() {
        // Given
        SearchRequestDTO request = TestDataBuilder.createTestSearchRequestDTO();
        RuntimeException publisherException = new RuntimeException("Publisher error");
        doThrow(publisherException).when(publisher).publishSearch(any(Search.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> searchUseCase.createSearch(request),
                "Debe propagar la excepción del publisher");

        assertAll("Publisher exception propagation verification",
                () -> assertEquals("Publisher error", exception.getMessage(),
                        "El mensaje de error debe ser propagado"),
                () -> verify(publisher, times(1)).publishSearch(any(Search.class)),
                () -> verifyNoInteractions(repository)
        );
    }

    @Test
    void shouldValidateDatesCorrectly_WhenValidDatesProvided() {
        // Given
        LocalDate checkIn = LocalDate.of(2024, 12, 25);
        LocalDate checkOut = LocalDate.of(2024, 12, 30);
        SearchRequestDTO request = TestDataBuilder.createTestSearchRequestDTOWithDates(checkIn, checkOut);
        doNothing().when(publisher).publishSearch(any(Search.class));

        // When & Then
        assertAll("Valid dates verification",
                () -> assertDoesNotThrow(() -> searchUseCase.createSearch(request),
                        "No debe lanzar excepción con fechas válidas"),
                () -> verify(publisher, times(1)).publishSearch(any(Search.class))
        );
    }
}
