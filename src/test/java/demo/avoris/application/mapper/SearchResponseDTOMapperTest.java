package demo.avoris.application.mapper;

import demo.avoris.TestDataBuilder;
import demo.avoris.application.dto.SearchResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SearchResponseDTOMapperTest {

    @Test
    void shouldCreateSearchResponseDTO_WhenSearchIdProvided() {
        // Given
        String searchId = TestDataBuilder.TEST_SEARCH_ID;

        // When
        SearchResponseDTO result = SearchResponseDTOMapper.toSearchResponseDTO(searchId);

        // Then
        assertAll("SearchResponseDTO creation verification",
                () -> assertNotNull(result,
                        "El SearchResponseDTO no debe ser null"),
                () -> assertEquals(searchId, result.searchId(),
                        "El searchId debe coincidir con el proporcionado")
        );
    }

    @Test
    void shouldCreateSearchResponseDTO_WhenDifferentSearchIdsProvided() {
        // Given
        String searchId1 = "search-123";
        String searchId2 = "search-456";

        // When
        SearchResponseDTO result1 = SearchResponseDTOMapper.toSearchResponseDTO(searchId1);
        SearchResponseDTO result2 = SearchResponseDTOMapper.toSearchResponseDTO(searchId2);

        // Then
        assertAll("Multiple SearchResponseDTO creation verification",
                () -> assertEquals(searchId1, result1.searchId(),
                        "El primer searchId debe coincidir"),
                () -> assertEquals(searchId2, result2.searchId(),
                        "El segundo searchId debe coincidir"),
                () -> assertNotNull(result1,
                        "El primer SearchResponseDTO no debe ser null"),
                () -> assertNotNull(result2,
                        "El segundo SearchResponseDTO no debe ser null")
        );
    }

    @Test
    void shouldCreateSearchResponseDTO_WhenDynamicSearchIdProvided() {
        // Given
        String dynamicSearchId = TestDataBuilder.generateRandomId();

        // When
        SearchResponseDTO result = SearchResponseDTOMapper.toSearchResponseDTO(dynamicSearchId);

        // Then
        assertAll("Dynamic SearchResponseDTO creation verification",
                () -> assertNotNull(result,
                        "El SearchResponseDTO no debe ser null"),
                () -> assertEquals(dynamicSearchId, result.searchId(),
                        "El searchId dinámico debe coincidir"),
                () -> assertNotNull(result.searchId(),
                        "El searchId no debe ser null")
        );
    }
}
