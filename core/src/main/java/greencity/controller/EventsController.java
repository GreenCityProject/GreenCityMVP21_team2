package greencity.controller;

import greencity.annotations.*;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econews.UpdateEcoNewsDto;
import greencity.dto.events.AddEventDtoRequest;
import greencity.dto.events.EventDto;
import greencity.dto.user.UserVO;
import greencity.service.EventsService;
import greencity.service.FileService;
import greencity.service.TagsService;
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
    private final TagsService tagService;
    private final FileService fileService;

    /**
     * Method for getting all events.
     *
     * @return {@link } instance.
     * @author
     */
    @Operation(summary = "Get all events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @ApiPageable
    @GetMapping("")
    public ResponseEntity<PageableAdvancedDto<EventDto>> findAll(@Parameter(hidden = true) Pageable page) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventsService.findAll(page));
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
    public ResponseEntity<EventDto> getEvent (@PathVariable Long eventId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventsService.findEventById(eventId));
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
     * Method for updating {@link EventDto}.
     *
     * @param  {@link EventDto} entity.
     * @return dto {@link EventDto} instance.
     */
    @Operation(summary = "Update event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = EventDto.class))),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PutMapping(path = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EventDto> update(
            @Parameter(description = SwaggerExampleModel.UPDATE_EVENT,
                    required = true) @RequestPart EventDto eventDto,
            @Parameter(description = "Image of events") @Valid @RequestPart(
                    required = false) List<MultipartFile> images,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(
                eventsService.update(eventDto, images, user.getId()));
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
}
