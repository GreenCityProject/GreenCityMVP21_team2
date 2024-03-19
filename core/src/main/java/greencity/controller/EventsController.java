package greencity.controller;

import greencity.annotations.*;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.PageableAdvancedDto;
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
}
