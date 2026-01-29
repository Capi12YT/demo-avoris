package demo.avoris.infrastructure.adapter.out.mongo;

import demo.avoris.application.port.out.SearchRepositoryPort;
import demo.avoris.domain.model.Search;
import demo.avoris.infrastructure.adapter.out.mongo.document.SearchDocument;
import demo.avoris.infrastructure.adapter.out.mongo.exeption.SearchNotFoundException;
import demo.avoris.infrastructure.mapper.SearchDocumentMapper;
import org.springframework.stereotype.Repository;


@Repository
public class SearchMongoRepositoryAdapter implements SearchRepositoryPort {

    private final SearchRepository repository;

    public SearchMongoRepositoryAdapter(SearchRepository repository) {
        this.repository = repository;
    }

    @Override
    public Search save(Search search) {
        SearchDocument searchDocument = SearchDocumentMapper.toDocument(search);

        SearchDocument savedDocument = repository.save(searchDocument);

        return SearchDocumentMapper.toDomain(savedDocument);
    }

    @Override
    public Search findBySearchId(String searchId) {
        SearchDocument searchDocument = repository.findBySearchId(searchId)
                .orElseThrow(() -> new SearchNotFoundException("Search with id " + searchId + " not found"));
        return SearchDocumentMapper.toDomain(searchDocument);
    }
}
