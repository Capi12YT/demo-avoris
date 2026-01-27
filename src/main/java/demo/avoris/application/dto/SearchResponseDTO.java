package demo.avoris.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Search creation response")
public record SearchResponseDTO(

        @Schema(example = "xxxxx")
        String searchId
) {
}
