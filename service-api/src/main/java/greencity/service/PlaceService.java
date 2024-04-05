package greencity.service;

import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlaceUpdateDto;

/**
 * Provides the interface to manage {@code Place} entity.
 */
public interface PlaceService {

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
}
