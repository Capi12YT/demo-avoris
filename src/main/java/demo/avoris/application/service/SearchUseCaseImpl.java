package demo.avoris.application.service;

import demo.avoris.application.dto.*;
import demo.avoris.application.mapper.SearchDetailResponseDTOMapper;
import demo.avoris.application.mapper.SearchRequestMapper;
import demo.avoris.application.mapper.SearchResponseDTOMapper;
import demo.avoris.application.port.in.SearchUseCase;
import demo.avoris.application.port.out.SearchEventPublisherPort;
import demo.avoris.application.port.out.SearchRepositoryPort;
import demo.avoris.domain.exception.InvalidCheckIn;

import demo.avoris.domain.model.Search;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SearchUseCaseImpl implements SearchUseCase {

    private final SearchRepositoryPort repository;

    private final SearchEventPublisherPort publisher;

    public SearchUseCaseImpl(SearchRepositoryPort repository, SearchEventPublisherPort publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public SearchResponseDTO createSearch(SearchRequestDTO request) {

        validatacionDates(request.checkIn(), request.checkOut());

        Search search = SearchRequestMapper.toDomain(request);

        publisher.publishSearch(search);

        return SearchResponseDTOMapper.toSearchResponseDTO(search.searchId());
    }



    @Override
    public SearchDetailResponseDTO getHotelCount(String searchId) {

        Search search = repository.findBySearchId(searchId);

        return SearchDetailResponseDTOMapper.toSearchDetailResponseDTO(search);
    }

    @Override
    public Search saveSearch(Search search) {
        return repository.save(search);
    }


    private void validatacionDates(LocalDate checkIn,LocalDate checkOut){
        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new InvalidCheckIn("Check-out date must be after check-in date.");
        }
    }
}
