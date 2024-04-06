package greencity.dto.place;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class AddPlaceDto {
    @NotBlank
    private String categoryName;

    @NotBlank
    private String locationName;

    @NotBlank
    private String placeName;

    @NotNull
    private List<OpeningHoursDto> openingHoursList;
}
