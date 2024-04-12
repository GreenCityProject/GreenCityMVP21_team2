package greencity.service;

import greencity.dto.place.AddPlaceDto;
import greencity.dto.place.PlaceResponse;
import greencity.dto.user.UserVO;

import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlaceUpdateDto;

/**
 * Provides the interface to manage {@code Place} entity.
 */
public interface PlaceService {
    PlaceResponse createPlace(UserVO user, AddPlaceDto addPlace);

    /**
     * Method to getting {@link PlaceInfoDto} specified by id.
     *
     * @param id of the {@link PlaceInfoDto} entity to retrieve.
     */
    PlaceInfoDto getInfo(Long id);

    /**
     * Method to getting {@link PlaceUpdateDto} specified by id.
     *
     * @param id of the {@link PlaceUpdateDto} entity to retrieve.
     */

    PlaceUpdateDto getPlaceById(Long id);

    PlaceResponse proposedCafePlace(UserVO user, AddPlaceDto addPlace);
}
