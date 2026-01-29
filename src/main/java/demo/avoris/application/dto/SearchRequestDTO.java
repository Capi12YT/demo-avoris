package demo.avoris.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        @Schema(example = "29/12/2023", description = "dd/MM/yyyy")
        LocalDate checkIn,

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        @Schema(example = "31/12/2023", description = "dd/MM/yyyy")
        LocalDate checkOut,

        @NotNull
        @Schema(example = "[30, 29, 1, 3]")
        List<Integer> ages
) {
    public SearchRequestDTO {
        ages = List.copyOf(ages);
    }
}

