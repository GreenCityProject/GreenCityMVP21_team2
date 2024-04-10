package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.dto.place.*;
import greencity.entity.*;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class AdminPlaceDtoMapper extends AbstractConverter<Place, AdminPlaceDto> {
    @Override
    protected AdminPlaceDto convert(Place place) {
        return AdminPlaceDto.builder()
                .id(place.getId())
                .status(place.getStatus())
                .name(place.getName())
                .modifiedDate(place.getModifiedAt())
                .category(maToCategoryDto(place.getCategory()))
                .openingHoursList(mapToOpeningHoursDto(place.getOpeningHours()))
                .author(mapToPlaceAuthorDto(place.getAuthor()))
                .location(mapToLocationDto(place.getLocation()))
                .build();
    }

    private static CategoryDto maToCategoryDto(Category category) {
        return CategoryDto.builder()
                .name(category.getName())
                .nameUa(category.getNameUa())
                .parentCategoryId(isNull(category.getParentCategory()) ? null : category.getId())
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
                .breakTime(nonNull(h.getBreakTime()) ? getBreakTime(h) : null)
                .build();
    }

    private static BreakTimeDto getBreakTime(OpeningHours h) {
        return BreakTimeDto.builder()
                .startTime(h.getBreakTime().getStartTime())
                .endTime(h.getBreakTime().getEndTime())
                .build();
    }

    private PlaceAuthorDto mapToPlaceAuthorDto(User author) {
        return PlaceAuthorDto.builder()
                .id(author.getId())
                .email(author.getEmail())
                .name(author.getName())
                .build();
    }

    private LocationDto mapToLocationDto(PlaceLocations location) {
        return LocationDto.builder()
                .id(location.getId())
                .address(location.getAddress())
                .lat(location.getLat())
                .lng(location.getLng())
                .build();
    }
}
