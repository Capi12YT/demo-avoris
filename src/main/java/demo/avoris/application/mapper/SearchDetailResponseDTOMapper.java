package demo.avoris.application.mapper;

import demo.avoris.application.dto.SearchDTO;
import demo.avoris.application.dto.SearchDetailResponseDTO;
import demo.avoris.domain.model.Search;

public class SearchDetailResponseDTOMapper {


    private SearchDetailResponseDTOMapper() {
    }

    public static SearchDetailResponseDTO toSearchDetailResponseDTO(Search search) {

        return new SearchDetailResponseDTO(
                search.searchId(),
                new SearchDTO(
                        search.hotelId(),
                        search.checkIn(),
                        search.checkOut(),
                        search.ages()
                ),
                search.count()
        );
    }

}
