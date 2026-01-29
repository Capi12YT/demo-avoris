package demo.avoris.infrastructure.adapter.out.mongo;

import demo.avoris.infrastructure.adapter.out.mongo.document.SearchDocument;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;


public interface SearchRepository  extends MongoRepository<SearchDocument, String> {
    Optional<SearchDocument> findBySearchId(String searchId);
}

