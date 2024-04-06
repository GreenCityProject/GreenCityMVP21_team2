package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.dto.place.AddPlaceLocation;
import greencity.dto.place.BreakTimeDto;
import greencity.dto.place.OpeningHoursDto;
import greencity.dto.place.PlaceResponse;
import greencity.entity.Category;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.entity.PlaceLocations;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.*;

@Component
public class PlaceResponseMapper extends AbstractConverter<Place, PlaceResponse> {

    @Override
    protected PlaceResponse convert(Place place) {
        return PlaceResponse.builder()
                .placeName(place.getName())
                .category(maToCategoryDto(place.getCategory()))
                .locationAddressAndGeoDto(mapToAddPlaceLocation(place.getLocation()))
                .openingHoursList(mapToOpeningHoursDto(place.getOpeningHours()))
                .build();
    }

    private static CategoryDto maToCategoryDto(Category category) {
        return CategoryDto.builder()
                .name(category.getName())
                .nameUa(category.getNameUa())
                .parentCategoryId(isNull(category.getParentCategory()) ? null : category.getId())
                .build();
    }

    private static AddPlaceLocation mapToAddPlaceLocation(PlaceLocations location) {
        return AddPlaceLocation.builder()
                .address(location.getAddressUa())
                .addressEng(location.getAddress())
                .lat(location.getLat())
                .lng(location.getLng())
                .build();
    }

    private List<OpeningHoursDto> mapToOpeningHoursDto(List<OpeningHours> openingHours) {
        return openingHours.stream()
                .map(this::mapOpeningHour)
                .collect(Collectors.toList());
    }

    private OpeningHoursDto mapOpeningHour(OpeningHours h) {
        return OpeningHoursDto.builder()
                .weekDay(h.getWeekDay())
                .openTime(h.getOpenTime())
                .closeTime(h.getCloseTime())
                .breakTime(BreakTimeDto.builder()
                        .startTime(h.getBreakTime().getStartTime())
                        .endTime(h.getBreakTime().getEndTime())
                        .build()
                ).build();
    }
}
