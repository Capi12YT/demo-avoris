package demo.avoris.application.mapper;

import demo.avoris.application.dto.SearchRequestDTO;
import demo.avoris.domain.model.Search;

import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class SearchRequestMapper {

    static DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static Search toDomain(SearchRequestDTO searchRequestDTO) {
        return new Search(
                UUID.randomUUID().toString(),
                searchRequestDTO.hotelId(),
                searchRequestDTO.checkIn(),
                searchRequestDTO.checkOut(),
                searchRequestDTO.ages(),
                1
        );
    }

}
