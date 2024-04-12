package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.events.*;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventsService {

    /**
     * Method for creating {@link EventDto} instance.
     *
     * @param addEventDtoRequest .
     * @return {@link EventDto} instance.
     */
    EventDto save(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, Long userId);

    /**
     * Method for getting all events by page.
     *
     * @param page parameters of to search.
     * @return PageableDto of {@link EventDto} instances.
     */
    PageableAdvancedDto<EventDto> findAll(Pageable page, Long userId);

    /**
     * Method for getting all events by page.
     *
     * @param user organizer of event.
     * @param page parameters of to search.
     * @return PageableDto of {@link EventDto} instances.
     */
    PageableAdvancedDto<EventDto> findAllEventsCreatedByUser(Pageable page, UserVO user);

    /**
     * Method for getting all events by page.
     *
     * @param user organizer and attender of event.
     * @param page parameters of to search.
     * @return PageableDto of {@link EventDto} instances.
     */
    PageableAdvancedDto<EventDto> findAllRelatedToUserEvents(UserVO user, Pageable page);

    /**
     * Method for getting all events by page.
     *
     * @param user attender of event.
     * @param page parameters of to search.
     * @return PageableDto of {@link EventDto} instances.
     */
    PageableAdvancedDto<EventDto> findAllUserEvents(UserVO user, Pageable page);

    /**
     * Method for getting the {@link EventVO} instance by its id.
     *
     * @param id {@link EventVO} instance id.
     * @return {@link EventVO} instance.
     */
    EventVO findById(Long id);

    /**
     * Method for getting the {@link EventDto} instance by its id.
     *
     * @param id {@link EventDto} instance id.
     * @return {@link EventDto} instance.
     */
    EventDto findEventById(Long id, Long userId);

    /**
     * Method for getting all {@link EventDto} attenders.
     *
     * @param id {@link EventDto} instance id.
     * @return list of {@link EventAttenderDto} instance.
     */
    List<EventAttenderDto> findAllEventAttenders(Long id);

    /**
     * Method for adding an attender to the {@link EventDto}.
     *
     * @param id   - {@link EventDto} instance id.
     * @param user for adding an attender to event.
     */
    void addAttender (Long id, UserVO user);

    /**
     * Method for adding an {@link EventDto} to favorites.
     *
     * @param id   - {@link EventDto} instance id.
     * @param user for adding an event to favorites.
     */
    void addToFavorites (Long id, UserVO user);

    /**
     * Method for removing an attender from the {@link EventDto}.
     *
     * @param id   - {@link EventDto} instance id.
     * @param user for removing an attender to event.
     */
    void removeAttender (Long id, UserVO user);

    /**
     * Method for removing an {@link EventDto} from favorites.
     *
     * @param id   - {@link EventDto} instance id.
     * @param user for removing an event from favorites.
     */
    void removeFromFavorites (Long id, UserVO user);

    /**
     * Method for deleting the {@link EventDto} instance by its id.
     *
     * @param id   - {@link EventDto} instance id which will be deleted.
     */
    void delete(Long id, UserVO user);

    /**
     * Method for updating {@link EventDto} instance.
     *
     * @param eventDto - {@link EventDto} instance.
     * @return instance of {@link EventDto}.
     */
    EventDto update(EventDtoToUpdate eventDto, List<MultipartFile> images, UserVO user);

    /**
     * A method for a user to rate an event based on the provided event identifier
     * and a rating represented as an integer ranging from 1 to 3.
     *
     */
    void rateEvent(Long eventId, int grade, UserVO userVO);

    /**
     * Method for getting all events by filter.
     *
     *
     */
    PageableAdvancedDto<EventDto>  getFilteredDataForManagementByPage(
            Pageable pageable, EventViewDto eventViewDto, UserVO userVO);
}
