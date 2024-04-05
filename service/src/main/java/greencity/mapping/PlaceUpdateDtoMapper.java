package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.dto.place.*;
import greencity.dto.specification.SpecificationNameDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.PlaceLocations;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class PlaceUpdateDtoMapper extends AbstractConverter<Place, PlaceUpdateDto> {

    @Override
    protected PlaceUpdateDto convert(Place place) {
        Category category = place.getCategory();
        PlaceLocations placeLocations = place.getLocation();
        return PlaceUpdateDto.builder()
                .id(place.getId())
                .name(place.getName())
                .category(CategoryDto.builder()
                        .name(category.getName())
                        .nameUa(category.getNameUa())
                        .parentCategoryId(category.getParentCategory() != null ?
                                category.getParentCategory().getId() : null)
                        .build())
                .location(LocationAddressAndGeoDto.builder()
                        .lng(placeLocations.getLng())
                        .lat(placeLocations.getLat())
                        .address(placeLocations.getAddress())
                        .build())
                .discountValues(place.getDiscountValues().stream().map(discountValue -> DiscountValueDto.builder()
                        .value(discountValue.getDiscountValue().getValue())
                        .specification(new SpecificationNameDto(discountValue.getName()))
                        .build()).toList())
                .openingHoursList(place.getOpeningHours().stream()
                        .map(openingHours -> OpeningHoursDto.builder()
                                .breakTime(BreakTimeDto.builder()
                                        .startTime(String.valueOf(openingHours.getBreakTime().getStartTime()))
                                        .endTime(openingHours.getBreakTime().getEndTime().toString())
                                        .build())
                                .openTime(openingHours.getOpenTime().toString())
                                .closeTime(openingHours.getCloseTime().toString())
                                .weekDay(openingHours.getWeekDay().toString())
                                .build())
                        .toList())
                .build();
    }
}
