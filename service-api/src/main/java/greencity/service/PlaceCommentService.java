package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

public interface PlaceCommentService {

    /**
     * Method for creating {@link AddCommentDto}.
     *
     * @param placeId id of {@link PlaceInfoDto} to add comment to.
     * @param addCommentDto - dto for comment entity.
     */
    CommentReturnDto save(AddCommentDto addCommentDto,Long placeId, UserVO user);

    /**
     * Method to getting {@link CommentReturnDto} specified by id.
     *
     * @param id of the {@link CommentReturnDto} entity to retrieve.
     */
    CommentReturnDto getCommentById(Long id);

    /**
     * Method returns all {@link CommentReturnDto} by page.
     *
     */
    PageableDto<CommentReturnDto> getAllComments (Pageable pageable);

    /**
     * Method to delete certain {@link CommentReturnDto} specified by id.
     *
     * @param id of {@link CommentReturnDto} to delete.
     */
    void delete(Long id, UserVO user);
}
