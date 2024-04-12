package greencity.dto.place;

import greencity.dto.category.CategoryDto;
import greencity.enums.PlaceStatus;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdminPlaceDto {
    private Long id;
    private String name;
    private ZonedDateTime modifiedDate;
    private PlaceStatus status;
    private PlaceAuthorDto author;
    private CategoryDto category;
    private LocationDto location;
    private List<OpeningHoursDto> openingHoursList;
}
