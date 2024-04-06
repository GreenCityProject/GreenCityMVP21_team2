package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.place.AddPlaceDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.FilterPlaceDto;
import greencity.dto.place.PlaceResponse;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {@code Place} entity.
 */
public interface PlaceService {
    PlaceResponse createPlace(UserVO user, AddPlaceDto addPlace);

    PageableDto<AdminPlaceDto> filterPlaces(FilterPlaceDto filterPlaceDto, Pageable pageable);
}
