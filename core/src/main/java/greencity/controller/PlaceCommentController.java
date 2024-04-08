package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.dto.user.UserVO;
import greencity.service.PlaceCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PlaceCommentController {
    private final PlaceCommentService placeCommentService;

    /**
     * Method for creating {@link AddCommentDto}.
     *
     * @param placeId id of {@link PlaceInfoDto} to add comment to.
     * @param addCommentDto - dto for comment entity.
     * @return {@link CommentReturnDto}
     * @author Chekhovska Maryna
     */
    @Operation(summary = "Add comment.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = AddCommentDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/place/{placeId}/comments")
    public ResponseEntity<CommentReturnDto> save(@PathVariable Long placeId,
                                                          @Valid @RequestBody AddCommentDto addCommentDto,
                                                          @Parameter(hidden = true) @CurrentUser UserVO user){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                placeCommentService.save(addCommentDto, placeId, user));
    }

    /**
     * Method to getting {@link CommentReturnDto} specified by id.
     *
     * @param id of the {@link CommentReturnDto} entity to retrieve.
     * @author Chekhovska Maryna
     */
    @Operation(summary = "Get comment by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentReturnDto> getCommentById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(placeCommentService.getCommentById(id));
    }

    /**
     * Method returns all {@link CommentReturnDto} by page.
     *
     * @author Chekhovska Maryna
     */
    @Operation(summary = "Get comments by page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/comments")
    public ResponseEntity<PageableDto<CommentReturnDto>> getAllComments (
            @Parameter(hidden = true) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK)
                .body(placeCommentService.getAllComments(pageable));
    }

    /**
     * Method to delete certain {@link CommentReturnDto} specified by id.
     *
     * @param id of {@link CommentReturnDto} to delete.
     * @author Chekhovska Maryna
     */
    @Operation(summary = "Mark comment as deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/comments")
    public ResponseEntity<Object> delete(@RequestParam("id") Long id,
                                         @Parameter(hidden = true) @CurrentUser UserVO user){
        placeCommentService.delete(id, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
