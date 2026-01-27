package demo.avoris.infrastructure.adapter.out.mongo.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "search")
public record SearchDocument(
        String id,
        String searchId,
        HotelDTO hotel,
        Integer count
) {
}
