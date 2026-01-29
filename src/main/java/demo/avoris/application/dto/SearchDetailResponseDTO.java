package demo.avoris.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Search detail response")
public record SearchDetailResponseDTO(

        @Schema(example = "xxxxx")
        String searchId,

        SearchDTO search,

        @Schema(example = "100")
        int count
) {
}