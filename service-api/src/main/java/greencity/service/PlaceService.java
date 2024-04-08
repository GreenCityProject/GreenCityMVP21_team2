package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.place.*;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

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

    /**
     * Method for getting all places by filter.
     *
     * @return {@link PageableDto} of {@link PlaceInfoDto} instance.
     */
    PageableDto<PlaceInfoDto> filterPlaceBySearchPredicate(Pageable pageable, FilterPlaceDto filterDto);
}
