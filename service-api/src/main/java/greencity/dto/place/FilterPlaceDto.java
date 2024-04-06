package greencity.dto.place;

import greencity.dto.filter.FilterDiscountDto;
import greencity.dto.filter.FilterDistanceDto;
import greencity.enums.PlaceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FilterPlaceDto {
    String[] categories;
    FilterDiscountDto filterDiscountDto;
    FilterDistanceDto filterDistanceDto;
    MapBoundsDto mapBoundsDto;
    String searchReg;
    PlaceStatus placeStatus;
}
