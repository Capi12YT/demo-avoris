package demo.avoris.infrastructure.adapter.out.mongo;

import demo.avoris.infrastructure.adapter.out.mongo.document.SearchDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SearchRepository  extends MongoRepository<SearchDocument, String> {
    Optional<SearchDocument> findBySearchId(String searchId);
}

