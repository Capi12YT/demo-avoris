package demo.avoris.application.mapper;

import demo.avoris.TestDataBuilder;
import demo.avoris.application.dto.SearchRequestDTO;
import demo.avoris.domain.model.Search;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SearchRequestMapperTest {

    @Test
    void shouldMapSearchRequestDTOToSearch_WhenValidRequestProvided() {
        // Given
        SearchRequestDTO requestDTO = TestDataBuilder.createTestSearchRequestDTO();

        // When
        Search result = SearchRequestMapper.toDomain(requestDTO);

        // Then
        assertAll("SearchRequestDTO to Search mapping verification",
                () -> assertNotNull(result.searchId(),
                        "El searchId debe ser generado automáticamente"),
                () -> assertDoesNotThrow(() -> UUID.fromString(result.searchId()),
                        "El searchId debe ser un UUID válido"),
                () -> assertEquals(requestDTO.hotelId(), result.hotelId(),
                        "El hotelId debe coincidir"),
                () -> assertEquals(requestDTO.checkIn(), result.checkIn(),
                        "La fecha de checkIn debe coincidir"),
                () -> assertEquals(requestDTO.checkOut(), result.checkOut(),
                        "La fecha de checkOut debe coincidir"),
                () -> assertEquals(requestDTO.ages(), result.ages(),
                        "Las edades deben coincidir"),
                () -> assertEquals(1, result.count(),
                        "El count debe inicializarse en 1")
        );
    }

    @Test
    void shouldGenerateUniqueIds_WhenMultipleMappingsExecuted() {
        // Given
        SearchRequestDTO requestDTO = TestDataBuilder.createTestSearchRequestDTO();

        // When
        Search result1 = SearchRequestMapper.toDomain(requestDTO);
        Search result2 = SearchRequestMapper.toDomain(requestDTO);

        // Then
        assertAll("Multiple mappings unique ID verification",
                () -> assertNotEquals(result1.searchId(), result2.searchId(),
                        "Los IDs generados deben ser únicos"),
                () -> assertNotNull(result1.searchId(),
                        "El primer searchId no debe ser null"),
                () -> assertNotNull(result2.searchId(),
                        "El segundo searchId no debe ser null"),
                () -> assertEquals(result1.hotelId(), result2.hotelId(),
                        "Los hotelIds deben ser iguales"),
                () -> assertEquals(result1.count(), result2.count(),
                        "Los counts deben ser iguales")
        );
    }

    @Test
    void shouldMapSearchRequestDTO_WhenDifferentDatesProvided() {
        // Given
        LocalDate customCheckIn = LocalDate.of(2025, 1, 15);
        LocalDate customCheckOut = LocalDate.of(2025, 1, 20);
        SearchRequestDTO requestDTO = TestDataBuilder.createTestSearchRequestDTOWithDates(customCheckIn, customCheckOut);

        // When
        Search result = SearchRequestMapper.toDomain(requestDTO);

        // Then
        assertAll("SearchRequestDTO mapping with custom dates verification",
                () -> assertEquals(customCheckIn, result.checkIn(),
                        "La fecha de checkIn personalizada debe coincidir"),
                () -> assertEquals(customCheckOut, result.checkOut(),
                        "La fecha de checkOut personalizada debe coincidir"),
                () -> assertEquals(requestDTO.hotelId(), result.hotelId(),
                        "El hotelId debe coincidir"),
                () -> assertEquals(requestDTO.ages(), result.ages(),
                        "Las edades deben coincidir")
        );
    }
}
