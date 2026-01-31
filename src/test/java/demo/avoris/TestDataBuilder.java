package demo.avoris;

import demo.avoris.application.dto.SearchDTO;
import demo.avoris.application.dto.SearchDetailResponseDTO;
import demo.avoris.application.dto.SearchRequestDTO;
import demo.avoris.application.dto.SearchResponseDTO;
import demo.avoris.domain.model.Search;
import demo.avoris.infrastructure.adapter.out.mongo.document.SearchData;
import demo.avoris.infrastructure.adapter.out.mongo.document.SearchDocument;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Clase utilitaria para crear objetos de prueba reutilizables
 */
public class TestDataBuilder {

    public static final String TEST_SEARCH_ID = "test-search-123";
    public static final String TEST_HOTEL_ID = "hotel-456";
    public static final LocalDate TEST_CHECK_IN = LocalDate.of(2024, 12, 25);
    public static final LocalDate TEST_CHECK_OUT = LocalDate.of(2024, 12, 30);
    public static final List<Integer> TEST_AGES = Arrays.asList(30, 25, 5, 3);
    public static final int TEST_COUNT = 1;
    public static final String TEST_DOCUMENT_ID = "doc-789";

    /**
     * Crea un objeto Search con datos predeterminados
     */
    public static Search createTestSearch() {
        return new Search(TEST_SEARCH_ID, TEST_HOTEL_ID, TEST_CHECK_IN, TEST_CHECK_OUT, TEST_AGES, TEST_COUNT);
    }

    /**
     * Crea un objeto Search con searchId personalizado
     */
    public static Search createTestSearchWithId(String searchId) {
        return new Search(searchId, TEST_HOTEL_ID, TEST_CHECK_IN, TEST_CHECK_OUT, TEST_AGES, TEST_COUNT);
    }

    /**
     * Crea un objeto Search con fechas personalizadas
     */
    public static Search createTestSearchWithDates(LocalDate checkIn, LocalDate checkOut) {
        return new Search(TEST_SEARCH_ID, TEST_HOTEL_ID, checkIn, checkOut, TEST_AGES, TEST_COUNT);
    }

    /**
     * Crea un objeto Search con count personalizado
     */
    public static Search createTestSearchWithCount(int count) {
        return new Search(TEST_SEARCH_ID, TEST_HOTEL_ID, TEST_CHECK_IN, TEST_CHECK_OUT, TEST_AGES, count);
    }

    /**
     * Crea un SearchRequestDTO con datos predeterminados
     */
    public static SearchRequestDTO createTestSearchRequestDTO() {
        return new SearchRequestDTO(TEST_HOTEL_ID, TEST_CHECK_IN, TEST_CHECK_OUT, TEST_AGES);
    }

    /**
     * Crea un SearchRequestDTO con fechas personalizadas
     */
    public static SearchRequestDTO createTestSearchRequestDTOWithDates(LocalDate checkIn, LocalDate checkOut) {
        return new SearchRequestDTO(TEST_HOTEL_ID, checkIn, checkOut, TEST_AGES);
    }

    /**
     * Crea un SearchResponseDTO con datos predeterminados
     */
    public static SearchResponseDTO createTestSearchResponseDTO() {
        return new SearchResponseDTO(TEST_SEARCH_ID);
    }

    /**
     * Crea un SearchDTO con datos predeterminados
     */
    public static SearchDTO createTestSearchDTO() {
        return new SearchDTO(TEST_HOTEL_ID, TEST_CHECK_IN, TEST_CHECK_OUT, TEST_AGES);
    }

    /**
     * Crea un SearchDetailResponseDTO con datos predeterminados
     */
    public static SearchDetailResponseDTO createTestSearchDetailResponseDTO() {
        return new SearchDetailResponseDTO(TEST_SEARCH_ID, createTestSearchDTO(), TEST_COUNT);
    }

    /**
     * Crea un SearchDocument con datos predeterminados
     */
    public static SearchDocument createTestSearchDocument() {
        return new SearchDocument(
            TEST_DOCUMENT_ID,
            TEST_SEARCH_ID,
            new SearchData(TEST_HOTEL_ID, "25/12/2024", "30/12/2024", TEST_AGES),
            TEST_COUNT
        );
    }

    /**
     * Crea un SearchData con datos predeterminados
     */
    public static SearchData createTestSearchData() {
        return new SearchData(TEST_HOTEL_ID, "25/12/2024", "30/12/2024", TEST_AGES);
    }

    /**
     * Genera un UUID aleatorio para tests
     */
    public static String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Crea fechas inválidas para tests de excepción (checkIn después de checkOut)
     */
    public static class InvalidDates {
        public static final LocalDate INVALID_CHECK_IN = LocalDate.of(2024, 12, 30);
        public static final LocalDate INVALID_CHECK_OUT = LocalDate.of(2024, 12, 25);
    }

    /**
     * Crea fechas iguales para tests de excepción
     */
    public static class EqualDates {
        public static final LocalDate SAME_DATE = LocalDate.of(2024, 12, 25);
    }
}
