package greencity.service;

import greencity.dto.place.*;
import greencity.dto.user.UserVO;

import java.util.List;
import java.util.Locale;

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

    List<PlaceInfoDto> getAllPlaceByName (SearchPlaceDto searchPlaceDto, UserVO user, Locale locale);
}
