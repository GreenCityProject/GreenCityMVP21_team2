package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.events.AddEventDtoRequest;
import greencity.dto.events.EventDto;
import greencity.dto.events.EventVO;
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
    PageableAdvancedDto<EventDto> findAll(Pageable page);

    /**
     * Method for getting the {@link EventVO} instance by its id.
     *
     * @param id {@link EventVO} instance id.
     * @return {@link EventVO} instance.
     */
    EventVO findById(Long id);

    /**
     * Method for deleting the {@link EventDto} instance by its id.
     *
     * @param id   - {@link EventDto} instance id which will be deleted.
     */
    void delete(Long id);

    /**
     * Method for updating {@link EventDto} instance.
     *
     * @param {@link EventDto}.
     * @return instance of {@link EventDto};=.
     */
    EventDto update(EventDto eventDto, List<MultipartFile> images, Long userId);

    /**
     * Method for rating the {@link EventDto} instance by its id.
     *
     * @param id   - {@link EventDto} instance id which will be rate.
     */
    void rate(Long id, int grade);
}
