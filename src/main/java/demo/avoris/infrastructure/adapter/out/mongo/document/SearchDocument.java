package demo.avoris.infrastructure.adapter.out.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "search")
public record SearchDocument(

        @Id
        String id,

        String searchId,

        SearchData search,

        int count
) {
}
