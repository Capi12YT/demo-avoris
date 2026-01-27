package demo.avoris.infrastructure.mapper;

import demo.avoris.domain.model.Hotel;
import demo.avoris.domain.model.Search;
import demo.avoris.infrastructure.adapter.out.mongo.document.SearchDocument;

import java.time.LocalDate;
import java.util.UUID;

public final class SearchMapper {

    private SearchMapper() {
    }

    public static SearchDocument toDocument(Search search) {
        return new SearchDocument(
                UUID.randomUUID().toString(), // ID técnico
                search.searchId(),
                search.hotel().hotelId(),
                search.hotel().name(),
                search.checkIn().toString(),
                search.checkOut().toString(),
                search.count()
        );
    }

    public static Search toDomain(SearchDocument document) {
        return new Search(
                document.searchId(),
                LocalDate.parse(document.checkIn()),
                LocalDate.parse(document.checkOut()),
                new Hotel(
                        document.hotelId(),
                        document.hotelName()
                ),
                document.count()
        );
    }
}

