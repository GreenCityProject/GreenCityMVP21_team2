package greencity.dto.place;

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

    private String categoryName;

    private String locationName;

    private String placeName;

    private List<OpeningHoursDto> openingHoursList;
}
