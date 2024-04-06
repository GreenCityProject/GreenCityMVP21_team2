package greencity.mapping;

import greencity.dto.comment.CommentDto;
import greencity.dto.place.*;
import greencity.dto.specification.SpecificationNameDto;
import greencity.entity.Place;
import greencity.entity.PlaceLocations;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class PlaceInfoDtoMapper extends AbstractConverter<Place, PlaceInfoDto> {

    @Override
    protected PlaceInfoDto convert(Place place) {
        PlaceLocations placeLocations = place.getLocation();
        return PlaceInfoDto.builder()
                .id(place.getId())
                .name(place.getName())
                .rate(place.getRating())
                .location(LocationDto.builder()
                        .id(placeLocations.getId())
                        .address(placeLocations.getAddress())
                        .lat(placeLocations.getLat())
                        .lng(placeLocations.getLng())
                        .build())
                .discountValues(place.getDiscountValues().stream().map(discountValue -> DiscountValueDto.builder()
                        .value(discountValue.getDiscountValue().getValue())
                        .specification(new SpecificationNameDto(discountValue.getName()))
                        .build()).toList())
                .openingHoursList(place.getOpeningHours().stream()
                        .map(openingHours -> OpenHoursDto.builder()
                                .id(openingHours.getId())
                                .breakTime(BreakTimeDto.builder()
                                        .startTime(openingHours.getBreakTime().getStartTime())
                                        .endTime(openingHours.getBreakTime().getEndTime())
                                        .build())
                                .openTime(openingHours.getOpenTime())
                                .closeTime(openingHours.getCloseTime())
                                .weekDay(openingHours.getWeekDay().toString())
                                .build())
                        .toList())
                .comments(place.getPlaceComments().stream().map(comment -> CommentDto.builder()
                        .text(comment.getText())
                        .build())
                        .toList())
                .build();
    }
}
