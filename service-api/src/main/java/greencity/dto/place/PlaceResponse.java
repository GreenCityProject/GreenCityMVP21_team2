package greencity.dto.place;

import greencity.dto.category.CategoryDto;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class PlaceResponse {

    private String placeName;

    private CategoryDto category;

    private AddPlaceLocation locationAddressAndGeoDto;

    private List<OpeningHoursDto> openingHoursList;
}
