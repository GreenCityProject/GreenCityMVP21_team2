package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.eventscomment.AddEventCommentDtoRequest;
import greencity.dto.eventscomment.AmountCommentLikesDto;
import greencity.dto.eventscomment.EventCommentDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

public interface EventCommentService {

    /**
     * Method for creating {@link EventCommentDto} instance.
     *
     * @param addEventCommentDtoRequest .
     * @param userVO current {@link EventCommentDto} author.
     * @return {@link AddEventCommentDtoRequest} instance.
     */
    AddEventCommentDtoRequest save(AddEventCommentDtoRequest addEventCommentDtoRequest, Long eventId, UserVO userVO);

    /**
     * Method to change the existing {@link EventCommentDto}.
     *
     * @param text new text of {@link EventCommentDto}.
     * @param id   to specify {@link EventCommentDto} that user wants to change.
     * @param userVO current {@link EventCommentDto} author.
     */
    void update( Long id, String text, UserVO userVO);

    /**
     * Method to mark {@link EventCommentDto} specified by id as deleted.
     *
     * @param id id of {@link EventCommentDto} to delete.
     * @param userVO current {@link EventCommentDto} author.
     */
    void delete(Long id, UserVO userVO);

    /**
     * Method to like or dislike {@link EventCommentDto} specified by id.
     *
     * @param id of {@link EventCommentDto} to like/dislike.
     * @param userVO current {@link EventCommentDto} author.
     */
    void like(Long id, UserVO userVO);

    /**
     * Method to getting {@link EventCommentDto} specified by id.
     *
     * @param id of the {@link EventCommentDto} entity to retrieve.
     */
    EventCommentDto getEventCommentById (Long id, UserVO userVO);

    /**
     * Method to getting count of {@link EventCommentDto}.
     *
     */
    Integer getCountOfComments (Long id);

    /**
     * Method to getting count likes for {@link EventCommentDto}.
     *
     */
    AmountCommentLikesDto countLikes (Long id, UserVO userVO);
    /**
     *  Method returns all active {@link EventCommentDto}.
     *
     */
    PageableDto<EventCommentDto> getAllActiveComments(Pageable pageable, UserVO user, Long eventId);
}
