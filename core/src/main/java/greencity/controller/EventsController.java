package greencity.controller;

import greencity.annotations.*;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.events.*;
import greencity.dto.user.UserVO;
import greencity.service.EventsService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventsController {
    private final EventsService eventsService;

    /**
     * Method for getting all events.
     *
     * @return {@link PageableAdvancedDto} of {@link EventDto} instance.
     * @author
     */
    @Operation(summary = "Get all events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @ApiPageable
    @GetMapping("")
    public ResponseEntity<PageableAdvancedDto<EventDto>> findAll(@Parameter(hidden = true) Pageable page,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventsService.findAll(page, user.getId()));
    }

    /**
     * Method for getting all events by filter.
     *
     * @return {@link PageableAdvancedDto} of {@link EventDto} instance.
     * @author
     */
    @Operation(summary = "Get all events by filter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @ApiPageable
    @GetMapping("/filtered")
    public ResponseEntity<PageableAdvancedDto<EventDto>> findAllByFilter(@Parameter(hidden = true) Pageable page,
                   @Parameter(description = SwaggerExampleModel.FILTER_EVENT) EventViewDto eventViewDto,
                   @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventsService.getFilteredDataForManagementByPage(page,eventViewDto, user));
    }

    /**
     * Method for getting all events by organizer.
     *
     * @return {@link PageableAdvancedDto} of {@link EventDto} instance.
     * @author
     */
    @Operation(summary = "Get all events by organizer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @ApiPageable
    @GetMapping("/myEvents/createdEvents")
    public ResponseEntity<PageableAdvancedDto<EventDto>> getEventsCreatedByUser(@Parameter(hidden = true) Pageable page,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventsService.findAllEventsCreatedByUser(page, user));
    }

    /**
     * Method for getting all users related events.
     *
     * @return {@link PageableAdvancedDto} of {@link EventDto} instance.
     * @author
     */
    @Operation(summary = "Get all users events and events which were created by this user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @ApiPageable
    @GetMapping("/myEvents/relatedEvents")
    public ResponseEntity<PageableAdvancedDto<EventDto>> getRelatedToUserEvents(@Parameter(hidden = true) Pageable page,
                                                                                @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventsService.findAllRelatedToUserEvents(user, page));
    }

    /**
     * Method for getting all {@link EventDto} where the user is an event attender.
     *
     * @return {@link PageableAdvancedDto} of {@link EventDto} instance.
     * @author
     */
    @Operation(summary = "Get all users events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @ApiPageable
    @GetMapping("/myEvents")
    public ResponseEntity<PageableAdvancedDto<EventDto>> getUserEvents(@Parameter(hidden = true) Pageable page,
               @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventsService.findAllUserEvents(user, page));
    }

    /**
     * Method for getting all {@link EventDto} attenders.
     *
     * @return list of {@link EventAttenderDto} instance.
     * @author
     */
    @Operation(summary = "Get all event attenders.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @ApiPageable
    @GetMapping("/getAllSubscribers/{eventId}")
    public ResponseEntity<List<EventAttenderDto>> getAllEventSubscribers(@PathVariable Long eventId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventsService.findAllEventAttenders(eventId));
    }

    /**
     * Method for getting event by id.
     *
     * @return {@link EventDto} instance.
     * @author
     */
    @Operation(summary = "Get the event by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @ApiPageable
    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventDto> getEvent (@PathVariable Long eventId,
                                              @Parameter(hidden = true) @CurrentUser UserVO user){
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventsService.findEventById(eventId, user.getId()));
    }

    /**
     * Method for creating {@link EventDto}.
     *
     * @param addEventDtoRequest - dto for {@link EventDto} entity.
     * @return dto {@link EventDto} instance.
     * @author .
     */
    @Operation(summary = "Create new event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
            content = @Content(schema = @Schema(implementation = EventDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping(path="/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventDto> save(
            @Parameter(description = SwaggerExampleModel.ADD_EVENT,
                    required = true) @RequestPart AddEventDtoRequest addEventDtoRequest,
            @Parameter(description = "Image of event") @Valid
            @RequestPart(required = false) List<MultipartFile> images,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                eventsService.save(addEventDtoRequest, images, user.getId()));
    }

    /**
     * Method for adding an attender to the {@link EventDto}.
     *
     */
    @Operation(summary = "Add an attender to the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/addAttender/{eventId}")
    public ResponseEntity<Object> addAttender (@PathVariable Long eventId,
                                               @Parameter(hidden = true) @CurrentUser UserVO user){
        eventsService.addAttender(eventId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for adding an {@link EventDto} to favorites.
     *
     */
    @Operation(summary = "Add an event to favorites.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/addToFavorites/{eventId}")
    public ResponseEntity<Object> addToFavorites (@PathVariable Long eventId,
                                               @Parameter(hidden = true) @CurrentUser UserVO user){
        eventsService.addToFavorites(eventId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for rating {@link EventDto} instance.
     * @param grade should be between 1 and 100;
     */
    @Operation(summary = "Rate event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/rateEvent/{eventId}/{grade}")
    public ResponseEntity<Object> rateEvent (@PathVariable Long eventId,
                                                  @PathVariable Integer grade){
        eventsService.rateEvent(eventId, grade);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for updating {@link EventDto}.
     *
     * @param  {@link EventDto} entity.
     * @return dto {@link EventDto} instance.
     */
    @Operation(summary = "Update event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = EventDtoToUpdate.class))),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PutMapping(path = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EventDto> update(
            @Parameter(description = SwaggerExampleModel.UPDATE_EVENT,
                    required = true) @RequestPart EventDtoToUpdate eventDto,
            @Parameter(description = "Image of events") @Valid @RequestPart(
                    required = false) List<MultipartFile> images,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(
                eventsService.update(eventDto, images, user));
    }

    /**
     * Method for deleting {@link EventDto}.
     *
     *
     */
    @Operation(summary = "Delete event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventId,
                                         @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventsService.delete(eventId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for removing an attender from the {@link EventDto}.
     *
     *
     */
    @Operation(summary = "Remove an attender from the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/removeAttender/{eventId}")
    public ResponseEntity<Object> removeAttender(@PathVariable Long eventId,
                                         @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventsService.removeAttender(eventId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for removing an {@link EventDto} from favorites.
     *
     *
     */
    @Operation(summary = "Remove an event from favorites.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/removeFromFavorites/{eventId}")
    public ResponseEntity<Object> removeFromFavorites(@PathVariable Long eventId,
                                                 @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventsService.removeFromFavorites(eventId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
