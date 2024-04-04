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
public class PlaceWithUserDto {

    private Long id;

    private PlaceAuthorDto author;

    private CategoryDto category;

    private List<DiscountValueDto> discountValues;

    private LocationAddressAndGeoDto location;

    private String name;

    private List<OpeningHoursDto> openingHoursList;

    private List<PhotoAddDto> photos;

    private String status;
}
