package greencity.service;

import greencity.dto.place.AddPlaceDto;
import greencity.dto.place.PlaceResponse;
import greencity.dto.user.UserVO;

/**
 * Provides the interface to manage {@code Place} entity.
 */
public interface PlaceService {
    PlaceResponse createPlace(UserVO user, AddPlaceDto addPlace);
}
