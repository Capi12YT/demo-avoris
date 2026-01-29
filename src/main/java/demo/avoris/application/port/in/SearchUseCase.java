package demo.avoris.application.port.in;

import demo.avoris.application.dto.SearchDetailResponseDTO;
import demo.avoris.application.dto.SearchRequestDTO;
import demo.avoris.application.dto.SearchResponseDTO;
import demo.avoris.domain.model.Search;

public interface SearchUseCase {

    SearchResponseDTO createSearch(SearchRequestDTO request);

    SearchDetailResponseDTO getHotelCount(String searchId);

    Search saveSearch(Search search);
}

