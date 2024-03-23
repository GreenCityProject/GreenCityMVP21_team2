package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.eventscomment.AddEventCommentDtoRequest;
import greencity.dto.eventscomment.AmountCommentLikesDto;
import greencity.dto.eventscomment.EventCommentDto;
import greencity.dto.events.EventDto;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/events/comments")
@RequiredArgsConstructor
public class EventCommentController {

    private final EventCommentService eventCommentService;

    /**
     * Method for creating {@link EventCommentDto}.
     *
     * @param eventId id of {@link EventDto} to add comment to.
     * @param request   - dto for {@link EventCommentDto} entity.
     * @return dto {@link AddEventCommentDtoRequest}
     */
    @Operation(summary = "Add comment.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = AddEventCommentDtoRequest.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{eventId}")
    public ResponseEntity<AddEventCommentDtoRequest> save(@PathVariable Long eventId,
                                                          @Valid @RequestBody AddEventCommentDtoRequest request,
                                                          @Parameter(hidden = true) @CurrentUser UserVO user){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                eventCommentService.save(request, eventId, user));
    }

    /**
     * Method to update certain {@link EventCommentDto} specified by id.
     *
     * @param id of {@link EventCommentDto} to update.
     * @param text new text of {@link EventCommentDto}
     */
    @Operation(summary = "Update comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("")
    public ResponseEntity<Object> update(Long id, @RequestParam @NotBlank String text,
                       @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.update(id, text, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to delete certain {@link EventCommentDto} specified by id.
     *
     * @param eventCommentId of {@link EventCommentDto} to delete.
     *
     */
    @Operation(summary = "Mark comment as deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{eventCommentId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventCommentId,
                                         @Parameter(hidden = true) @CurrentUser UserVO user){
        eventCommentService.delete(eventCommentId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to like/dislike certain {@link EventCommentDto} specified by id.
     *
     * @param id of {@link EventCommentDto} to like/dislike
     */
    @Operation(summary = "Like/dislike comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/like")
    public ResponseEntity<Object> like(@RequestParam("id") Long id, @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.like(id, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to getting {@link EventCommentDto} specified by id.
     *
     * @param id of the {@link EventCommentDto} entity to retrieve.
     */
    @Operation(summary = "Get comment by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventCommentDto> getEventCommentById(@PathVariable Long id,
                                                               @Parameter(hidden = true) @CurrentUser UserVO user){
        return ResponseEntity.status(HttpStatus.OK).body(eventCommentService.getEventCommentById(id, user));
    }

    /**
     * Method to getting count of {@link EventCommentDto}.
     *
     */
    @Operation(summary = "Count comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/count/{eventId}")
    public ResponseEntity<Integer> getCountOfComments(@PathVariable Long eventId){
        return ResponseEntity.status(HttpStatus.OK).body(eventCommentService.getCountOfComments(eventId));
    }

    /**
     * Method to getting count likes for {@link EventCommentDto}.
     *
     */
    @Operation(summary = "Count likes for comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/likes/count/{commentId}")
    public ResponseEntity<AmountCommentLikesDto> countLikes(@PathVariable Long commentId ,
                                                            @Parameter(hidden = true) @CurrentUser UserVO user){
        return ResponseEntity.status(HttpStatus.OK).body(eventCommentService.countLikes(commentId, user));
    }

    /**
     * Method returns all active {@link EventCommentDto}..
     *
     */
    @Operation(summary = "Get all active comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/active")
    public ResponseEntity<PageableDto<EventCommentDto>> getAllActiveComments (
            @Parameter(hidden = true) Pageable pageable, Long eventId,
            @Parameter(hidden = true) @CurrentUser UserVO user){
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventCommentService.getAllActiveComments(pageable, user, eventId));
    }
}
