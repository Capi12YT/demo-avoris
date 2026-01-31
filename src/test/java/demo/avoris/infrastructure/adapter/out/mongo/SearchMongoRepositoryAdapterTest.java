package demo.avoris.infrastructure.adapter.out.mongo;

import demo.avoris.TestDataBuilder;
import demo.avoris.domain.model.Search;
import demo.avoris.infrastructure.adapter.out.mongo.document.SearchDocument;
import demo.avoris.infrastructure.adapter.out.mongo.exeption.SearchNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchMongoRepositoryAdapterTest {

    @Mock
    private SearchRepository repository;

    private SearchMongoRepositoryAdapter mongoAdapter;

    @BeforeEach
    void setUp() {
        mongoAdapter = new SearchMongoRepositoryAdapter(repository);
    }

    @Test
    void shouldSaveSearchSuccessfully_WhenValidSearchProvided() {
        // Given
        Search search = TestDataBuilder.createTestSearch();
        SearchDocument savedDocument = TestDataBuilder.createTestSearchDocument();

        when(repository.save(any(SearchDocument.class))).thenReturn(savedDocument);

        // When
        Search result = mongoAdapter.save(search);

        // Then
        assertAll("Save search success verification",
                () -> assertNotNull(result,
                        "El resultado no debe ser null"),
                () -> assertEquals(savedDocument.searchId(), result.searchId(),
                        "El searchId debe coincidir"),
                () -> assertEquals(savedDocument.search().hotelId(), result.hotelId(),
                        "El hotelId debe coincidir"),
                () -> assertEquals(savedDocument.count(), result.count(),
                        "El count debe coincidir"),
                () -> verify(repository, times(1)).save(any(SearchDocument.class))
        );
    }

    @Test
    void shouldFindBySearchIdSuccessfully_WhenSearchExists() {
        // Given
        String searchId = TestDataBuilder.TEST_SEARCH_ID;
        SearchDocument foundDocument = TestDataBuilder.createTestSearchDocument();

        when(repository.findBySearchId(searchId)).thenReturn(Optional.of(foundDocument));

        // When
        Search result = mongoAdapter.findBySearchId(searchId);

        // Then
        assertAll("Find by searchId success verification",
                () -> assertNotNull(result,
                        "El resultado no debe ser null"),
                () -> assertEquals(foundDocument.searchId(), result.searchId(),
                        "El searchId debe coincidir"),
                () -> assertEquals(foundDocument.search().hotelId(), result.hotelId(),
                        "El hotelId debe coincidir"),
                () -> assertEquals(foundDocument.count(), result.count(),
                        "El count debe coincidir"),
                () -> verify(repository, times(1)).findBySearchId(searchId)
        );
    }

    @Test
    void shouldThrowSearchNotFoundException_WhenSearchDoesNotExist() {
        // Given
        String nonExistentSearchId = "non-existent-search";

        when(repository.findBySearchId(nonExistentSearchId)).thenReturn(Optional.empty());

        // When & Then
        SearchNotFoundException exception = assertThrows(SearchNotFoundException.class,
                () -> mongoAdapter.findBySearchId(nonExistentSearchId),
                "Debe lanzar SearchNotFoundException cuando la búsqueda no existe");

        assertAll("SearchNotFoundException verification",
                () -> assertTrue(exception.getMessage().contains(nonExistentSearchId),
                        "El mensaje debe contener el searchId que no se encontró"),
                () -> assertTrue(exception.getMessage().contains("not found"),
                        "El mensaje debe indicar que no se encontró"),
                () -> verify(repository, times(1)).findBySearchId(nonExistentSearchId)
        );
    }

    @Test
    void shouldHandleMultipleSaveOperations_WhenCalledMultipleTimes() {
        // Given
        Search search1 = TestDataBuilder.createTestSearchWithId("search-1");
        Search search2 = TestDataBuilder.createTestSearchWithId("search-2");
        SearchDocument savedDocument1 = new SearchDocument("doc-1", "search-1", TestDataBuilder.createTestSearchData(), 1);
        SearchDocument savedDocument2 = new SearchDocument("doc-2", "search-2", TestDataBuilder.createTestSearchData(), 2);

        when(repository.save(any(SearchDocument.class)))
                .thenReturn(savedDocument1)
                .thenReturn(savedDocument2);

        // When
        Search result1 = mongoAdapter.save(search1);
        Search result2 = mongoAdapter.save(search2);

        // Then
        assertAll("Multiple save operations verification",
                () -> assertEquals("search-1", result1.searchId(),
                        "El primer searchId debe coincidir"),
                () -> assertEquals("search-2", result2.searchId(),
                        "El segundo searchId debe coincidir"),
                () -> assertEquals(1, result1.count(),
                        "El primer count debe coincidir"),
                () -> assertEquals(2, result2.count(),
                        "El segundo count debe coincidir"),
                () -> verify(repository, times(2)).save(any(SearchDocument.class))
        );
    }

    @Test
    void shouldHandleMultipleFindOperations_WhenCalledMultipleTimes() {
        // Given
        String searchId1 = "search-1";
        String searchId2 = "search-2";
        SearchDocument document1 = new SearchDocument("doc-1", searchId1, TestDataBuilder.createTestSearchData(), 1);
        SearchDocument document2 = new SearchDocument("doc-2", searchId2, TestDataBuilder.createTestSearchData(), 2);

        when(repository.findBySearchId(searchId1)).thenReturn(Optional.of(document1));
        when(repository.findBySearchId(searchId2)).thenReturn(Optional.of(document2));

        // When
        Search result1 = mongoAdapter.findBySearchId(searchId1);
        Search result2 = mongoAdapter.findBySearchId(searchId2);

        // Then
        assertAll("Multiple find operations verification",
                () -> assertEquals(searchId1, result1.searchId(),
                        "El primer searchId debe coincidir"),
                () -> assertEquals(searchId2, result2.searchId(),
                        "El segundo searchId debe coincidir"),
                () -> assertEquals(1, result1.count(),
                        "El primer count debe coincidir"),
                () -> assertEquals(2, result2.count(),
                        "El segundo count debe coincidir"),
                () -> verify(repository, times(1)).findBySearchId(searchId1),
                () -> verify(repository, times(1)).findBySearchId(searchId2)
        );
    }

    @Test
    void shouldPropagateRepositoryException_WhenSaveFails() {
        // Given
        Search search = TestDataBuilder.createTestSearch();
        RuntimeException repositoryException = new RuntimeException("Database connection error");

        when(repository.save(any(SearchDocument.class))).thenThrow(repositoryException);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mongoAdapter.save(search),
                "Debe propagar la excepción del repositorio");

        assertAll("Repository exception propagation verification",
                () -> assertEquals("Database connection error", exception.getMessage(),
                        "El mensaje de error debe ser propagado"),
                () -> verify(repository, times(1)).save(any(SearchDocument.class))
        );
    }

    @Test
    void shouldPerformCorrectMapping_BetweenDomainAndDocument() {
        // Given
        Search originalSearch = TestDataBuilder.createTestSearch();
        SearchDocument savedDocument = TestDataBuilder.createTestSearchDocument();

        when(repository.save(any(SearchDocument.class))).thenReturn(savedDocument);

        // When
        Search savedSearch = mongoAdapter.save(originalSearch);

        // Then
        assertAll("Domain to document mapping verification",
                () -> assertEquals(originalSearch.searchId(), savedSearch.searchId(),
                        "El searchId debe mantenerse en el mapping"),
                () -> assertEquals(originalSearch.hotelId(), savedSearch.hotelId(),
                        "El hotelId debe mantenerse en el mapping"),
                () -> assertEquals(originalSearch.checkIn(), savedSearch.checkIn(),
                        "La fecha checkIn debe mantenerse en el mapping"),
                () -> assertEquals(originalSearch.checkOut(), savedSearch.checkOut(),
                        "La fecha checkOut debe mantenerse en el mapping"),
                () -> assertEquals(originalSearch.ages(), savedSearch.ages(),
                        "Las edades deben mantenerse en el mapping")
        );
    }

    @Test
    void shouldHandleSearchesWithDifferentCounts_WhenSavingAndRetrieving() {
        // Given
        Search searchWithCount5 = TestDataBuilder.createTestSearchWithCount(5);
        SearchDocument documentWithCount5 = new SearchDocument(
                TestDataBuilder.TEST_DOCUMENT_ID,
                TestDataBuilder.TEST_SEARCH_ID,
                TestDataBuilder.createTestSearchData(),
                5
        );

        when(repository.save(any(SearchDocument.class))).thenReturn(documentWithCount5);
        when(repository.findBySearchId(TestDataBuilder.TEST_SEARCH_ID)).thenReturn(Optional.of(documentWithCount5));

        // When
        Search savedResult = mongoAdapter.save(searchWithCount5);
        Search foundResult = mongoAdapter.findBySearchId(TestDataBuilder.TEST_SEARCH_ID);

        // Then
        assertAll("Different counts handling verification",
                () -> assertEquals(5, savedResult.count(),
                        "El count guardado debe ser 5"),
                () -> assertEquals(5, foundResult.count(),
                        "El count encontrado debe ser 5"),
                () -> assertEquals(savedResult.count(), foundResult.count(),
                        "Los counts deben ser consistentes"),
                () -> verify(repository, times(1)).save(any(SearchDocument.class)),
                () -> verify(repository, times(1)).findBySearchId(TestDataBuilder.TEST_SEARCH_ID)
        );
    }
}
