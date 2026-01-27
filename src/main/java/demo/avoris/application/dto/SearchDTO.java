package demo.avoris.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Search details")
public record SearchDTO(

        @Schema(example = "1234aBc")
        String hotelId,

        @Schema(example = "29/12/2023")
        LocalDate checkIn,

        @Schema(example = "31/12/2023")
        LocalDate checkOut,

        @Schema(example = "[3, 29, 30, 1]")
        List<Integer> ages
) {
    public SearchDTO {
        ages = List.copyOf(ages);
    }
}

