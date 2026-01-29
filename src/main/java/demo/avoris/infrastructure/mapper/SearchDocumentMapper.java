package demo.avoris.infrastructure.mapper;


import demo.avoris.domain.model.Search;
import demo.avoris.infrastructure.adapter.out.mongo.document.SearchData;
import demo.avoris.infrastructure.adapter.out.mongo.document.SearchDocument;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public final class SearchDocumentMapper {

    static DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public SearchDocumentMapper() {
    }

    public static SearchDocument toDocument(Search search) {
        return new SearchDocument(
                UUID.randomUUID().toString(),
                search.searchId(),
                new SearchData(
                        search.hotelId(),
                        search.checkIn().format(pattern),
                        search.checkOut().format(pattern),
                        search.ages()
                ),
                search.count()
        );
    }

    public static Search toDomain(SearchDocument searchDocument) {
        return new Search(
                searchDocument.searchId(),
                searchDocument.search().hotelId(),
                LocalDate.parse(searchDocument.search().checkIn(), pattern),
                LocalDate.parse(searchDocument.search().checkOut(), pattern),
                searchDocument.search().ages(),
                searchDocument.count()
        );
    }


}

