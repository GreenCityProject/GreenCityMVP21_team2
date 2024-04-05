package greencity.service;

import greencity.dto.place.PlaceInfoDto;

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
}
