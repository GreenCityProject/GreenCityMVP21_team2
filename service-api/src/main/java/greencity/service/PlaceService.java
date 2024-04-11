package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.place.*;
import greencity.dto.user.UserVO;
import greencity.enums.EmailNotification;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    /**
     * Method subscribe for email notification about place updates
     *
     * @param placeSubscribeDto of the {@link PlaceSubscribeDto} contain sending frequency.
     * @param userVO of the {@link UserVO}
     * @return {@link PlaceSubscribeResponseDto} of {@link PlaceSubscribeResponseDto} instance.
     */
    PlaceSubscribeResponseDto subscribeEmailNotification(PlaceSubscribeDto placeSubscribeDto, UserVO userVO);

    /**
     * Method unsubscribe for email notification about place updates
     *
     * @param userVO of the {@link UserVO}
     * @return {@link PlaceSubscribeResponseDto} of {@link PlaceSubscribeResponseDto} instance.
     */
    PlaceSubscribeResponseDto unsubscribeEmailNotification(UserVO userVO);

    /**
     * Method update email notification sending frequency
     *
     * @param userVO of the {@link UserVO}
     * @param emailNotification of the {@link EmailNotification}
     * @return {@link PlaceSubscribeResponseDto} of {@link PlaceSubscribeResponseDto} instance.
     */
    PlaceSubscribeResponseDto updateEmailNotificationFrequency(UserVO userVO, EmailNotification emailNotification);

    /**
     * Method return list of places updates subscribers.
     *
     * @return list of {@link PlaceSubscribeResponseDto} instance.
     */
    List<PlaceSubscribeResponseDto> getAllPlaceUpdatesSubscribers();

    /**
     * Method return list of places updates subscribers by frequency.
     *
     * @return list of {@link PlaceSubscribeResponseDto} instance.
     */
    List<PlaceSubscribeResponseDto> getAllPlaceUpdatesSubscribersByFrequency(EmailNotification emailNotification);
}
