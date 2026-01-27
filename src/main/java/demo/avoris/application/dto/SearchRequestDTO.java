package demo.avoris.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Search request")
public record SearchRequestDTO(

        @NotNull
        @Schema(example = "1234aBc")
        String hotelId,

        @NotNull
        @Schema(example = "29/12/2023", description = "dd/MM/yyyy")
        LocalDate checkIn,

        @NotNull
        @Schema(example = "31/12/2023", description = "dd/MM/yyyy")
        LocalDate checkOut,

        @NotNull
        @Schema(example = "[30, 29, 1, 3]")
        List<Integer> ages
) {
    public SearchRequestDTO {
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException(
                    "Check-in date must be before check-out date"
            );
        }

        ages = List.copyOf(ages);
    }
}

