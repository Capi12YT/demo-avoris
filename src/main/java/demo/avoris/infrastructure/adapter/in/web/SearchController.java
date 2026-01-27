package demo.avoris.infrastructure.adapter.in.web;

import demo.avoris.application.port.in.SearchUseCase;
import demo.avoris.application.dto.SearchRequestDTO;
import demo.avoris.application.dto.SearchResponseDTO;
import demo.avoris.application.dto.CountResponseDTO;
import demo.avoris.domain.exception.HotelSearchNotFoundException;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@Tag(name = "Search", description = "Search operations")
public class SearchController {

    private final SearchUseCase searchUseCase;

    public SearchController(SearchUseCase searchUseCase) {
        this.searchUseCase = searchUseCase;
    }

    @Operation(summary = "Get search count by searchId")
    @GetMapping("/{searchId}")
    public ResponseEntity<CountResponseDTO> getSearchDetail(
            @PathVariable String searchId) {

        try {
            return ResponseEntity.ok(searchUseCase.getHotelCount(searchId));
        } catch (HotelSearchNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new search")
    @PostMapping
    public ResponseEntity<SearchResponseDTO> createSearch(
            @Valid @RequestBody SearchRequestDTO request) {

        String searchId = searchUseCase.createSearch(request);
        return ResponseEntity.ok(new SearchResponseDTO(searchId));
    }
}

