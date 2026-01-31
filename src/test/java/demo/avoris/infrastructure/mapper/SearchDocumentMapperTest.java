
package demo.avoris.infrastructure.mapper;

import demo.avoris.TestDataBuilder;
import demo.avoris.domain.model.Search;
import demo.avoris.infrastructure.adapter.out.mongo.document.SearchData;
import demo.avoris.infrastructure.adapter.out.mongo.document.SearchDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class SearchDocumentMapperTest {

    private static final DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Test
    void shouldMapSearchToDocument_WhenValidSearchProvided() {
        // Given
        Search search = TestDataBuilder.createTestSearch();

        // When
        SearchDocument result = SearchDocumentMapper.toDocument(search);

        // Then
        assertAll("Search to SearchDocument mapping verification",
                () -> assertNotNull(result.id(),
                        "El ID del documento debe ser generado"),
                () -> assertEquals(search.searchId(), result.searchId(),
                        "El searchId debe coincidir"),
                () -> assertEquals(search.hotelId(), result.search().hotelId(),
                        "El hotelId debe coincidir"),
                () -> assertEquals(search.checkIn().format(PATTERN), result.search().checkIn(),
                        "La fecha de checkIn debe estar formateada correctamente"),
                () -> assertEquals(search.checkOut().format(PATTERN), result.search().checkOut(),
                        "La fecha de checkOut debe estar formateada correctamente"),
                () -> assertEquals(search.ages(), result.search().ages(),
                        "Las edades deben coincidir"),
                () -> assertEquals(search.count(), result.count(),
                        "El count debe coincidir")
        );
    }

    @Test
    void shouldMapDocumentToSearch_WhenValidSearchDocumentProvided() {
        // Given
        SearchDocument document = TestDataBuilder.createTestSearchDocument();

        // When
        Search result = SearchDocumentMapper.toDomain(document);

        // Then
        assertAll("SearchDocument to Search mapping verification",
                () -> assertEquals(document.searchId(), result.searchId(),
                        "El searchId debe coincidir"),
                () -> assertEquals(document.search().hotelId(), result.hotelId(),
                        "El hotelId debe coincidir"),
                () -> assertEquals(LocalDate.parse(document.search().checkIn(), PATTERN), result.checkIn(),
                        "La fecha de checkIn debe ser parseada correctamente"),
                () -> assertEquals(LocalDate.parse(document.search().checkOut(), PATTERN), result.checkOut(),
                        "La fecha de checkOut debe ser parseada correctamente"),
                () -> assertEquals(document.search().ages(), result.ages(),
                        "Las edades deben coincidir"),
                () -> assertEquals(document.count(), result.count(),
                        "El count debe coincidir")
        );
    }

    @Test
    void shouldGenerateUniqueDocumentIds_WhenMultipleMappingsExecuted() {
        // Given
        Search search = TestDataBuilder.createTestSearch();

        // When
        SearchDocument result1 = SearchDocumentMapper.toDocument(search);
        SearchDocument result2 = SearchDocumentMapper.toDocument(search);

        // Then
        assertAll("Multiple document mapping unique ID verification",
                () -> assertNotEquals(result1.id(), result2.id(),
                        "Los IDs de documento deben ser únicos"),
                () -> assertNotNull(result1.id(),
                        "El primer ID no debe ser null"),
                () -> assertNotNull(result2.id(),
                        "El segundo ID no debe ser null"),
                () -> assertEquals(result1.searchId(), result2.searchId(),
                        "Los searchIds deben ser iguales"),
                () -> assertEquals(result1.search().hotelId(), result2.search().hotelId(),
                        "Los hotelIds deben ser iguales")
        );
    }

    @Test
    void shouldFormatDatesCorrectly_WhenMappingToDocument() {
        // Given
        LocalDate customCheckIn = LocalDate.of(2025, 2, 14);
        LocalDate customCheckOut = LocalDate.of(2025, 2, 20);
        Search search = TestDataBuilder.createTestSearchWithDates(customCheckIn, customCheckOut);

        // When
        SearchDocument result = SearchDocumentMapper.toDocument(search);

        // Then
        assertAll("Date formatting verification",
                () -> assertEquals("14/02/2025", result.search().checkIn(),
                        "La fecha de checkIn debe estar formateada como dd/MM/yyyy"),
                () -> assertEquals("20/02/2025", result.search().checkOut(),
                        "La fecha de checkOut debe estar formateada como dd/MM/yyyy"),
                () -> assertDoesNotThrow(() -> LocalDate.parse(result.search().checkIn(), PATTERN),
                        "La fecha de checkIn formateada debe ser parseable"),
                () -> assertDoesNotThrow(() -> LocalDate.parse(result.search().checkOut(), PATTERN),
                        "La fecha de checkOut formateada debe ser parseable")
        );
    }

    @Test
    void shouldPerformBidirectionalMapping_WhenSearchToDocumentAndBack() {
        // Given
        Search originalSearch = TestDataBuilder.createTestSearch();

        // When
        SearchDocument document = SearchDocumentMapper.toDocument(originalSearch);
        Search mappedBackSearch = SearchDocumentMapper.toDomain(document);

        // Then
        assertAll("Bidirectional mapping verification",
                () -> assertEquals(originalSearch.searchId(), mappedBackSearch.searchId(),
                        "El searchId debe mantenerse después del mapping bidireccional"),
                () -> assertEquals(originalSearch.hotelId(), mappedBackSearch.hotelId(),
                        "El hotelId debe mantenerse después del mapping bidireccional"),
                () -> assertEquals(originalSearch.checkIn(), mappedBackSearch.checkIn(),
                        "La fecha de checkIn debe mantenerse después del mapping bidireccional"),
                () -> assertEquals(originalSearch.checkOut(), mappedBackSearch.checkOut(),
                        "La fecha de checkOut debe mantenerse después del mapping bidireccional"),
                () -> assertEquals(originalSearch.ages(), mappedBackSearch.ages(),
                        "Las edades deben mantenerse después del mapping bidireccional"),
                () -> assertEquals(originalSearch.count(), mappedBackSearch.count(),
                        "El count debe mantenerse después del mapping bidireccional")
        );
    }

    @Test
    void shouldHandleDateFormatPattern_WhenAccessingStaticFormatter() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 12, 25);

        // When & Then
        assertAll("Date pattern verification",
                () -> assertDoesNotThrow(() -> testDate.format(SearchDocumentMapper.pattern),
                        "El patrón de fecha debe ser válido para formatear"),
                () -> assertEquals("25/12/2024", testDate.format(SearchDocumentMapper.pattern),
                        "El patrón debe formatear fechas correctamente"),
                () -> assertDoesNotThrow(() -> LocalDate.parse("25/12/2024", SearchDocumentMapper.pattern),
                        "El patrón debe ser válido para parsear fechas")
        );
    }
}
