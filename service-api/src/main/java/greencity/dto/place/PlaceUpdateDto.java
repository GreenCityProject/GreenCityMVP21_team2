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
public class PlaceUpdateDto {

    private Long id;

    private CategoryDto category;

    private List<DiscountValueDto> discountValues;

    private LocationAddressAndGeoDto location;

    private String name;

    private List<OpeningHoursDto> openingHoursList;
}
