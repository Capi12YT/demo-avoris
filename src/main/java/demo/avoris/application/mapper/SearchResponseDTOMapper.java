package demo.avoris.application.mapper;

import demo.avoris.application.dto.SearchResponseDTO;

public class SearchResponseDTOMapper {

    public SearchResponseDTOMapper() {
    }

    public static SearchResponseDTO toSearchResponseDTO(String searchId) {
        return new SearchResponseDTO(searchId);
    }
}
