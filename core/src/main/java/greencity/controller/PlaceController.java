package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.PageableDto;
import greencity.dto.place.*;
import greencity.dto.user.UserVO;
import greencity.enums.EmailNotification;
import greencity.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceController {
    public final PlaceService placeService;

    /**
     * The method which save new Place.
     *
     * @param addPlace - AddPlaceDto dto for adding with all parameters.
     * @return new {@link  PlaceResponse}.
     * @author Denys Ryhal
     */
    @Operation(summary = "Save new place")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/v2/save")
    public PlaceResponse savePlace(@Parameter(hidden = true) @CurrentUser UserVO user,
                                   @io.swagger.v3.oas.annotations.parameters.RequestBody(description = SwaggerExampleModel.ADD_PLACE_DTO)
                                   @RequestBody @Validated AddPlaceDto addPlace){
        return placeService.createPlace(user,addPlace);
    }

    /**
     * Method to getting {@link PlaceInfoDto} specified by id.
     *
     * @param id of the {@link PlaceInfoDto} entity to retrieve.
     * @author Chekhovska Maryna
     */
    @Operation(summary = "Get info about place.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/info/{id}")
    public ResponseEntity<PlaceInfoDto> getInfo(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.getInfo(id));
    }

    /**
     * Method to getting {@link PlaceUpdateDto} specified by id.
     *
     * @param id of the {@link PlaceUpdateDto} entity to retrieve.
     * @author Chekhovska Maryna
     */
    @Operation(summary = "Get place by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/about/{id}")
    public ResponseEntity<PlaceUpdateDto> getPlaceById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.getPlaceById(id));
    }

    /**
     * Method for getting all places by filter.
     *
     * @return {@link PageableDto} of {@link PlaceInfoDto} instance.
     */
    @Operation(summary = "Return a list places filtered by values contained in the incoming FilterPlaceDto object.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @ApiPageable
    @PostMapping("/filter/predicate")
    public ResponseEntity<PageableDto<PlaceInfoDto>> filterPlaceBySearchPredicate(
            @Parameter(hidden = true) Pageable page,
            @RequestBody FilterPlaceDto filterDto){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.filterPlaceBySearchPredicate(page, filterDto));
    }

    /**
     * Method for subscribing email notification about place updates.
     *
     * @param placeSubscribeDto of the {@link PlaceSubscribeResponseDto} with EmailNotification.
     * @return {@link PlaceSubscribeResponseDto} of {@link PlaceSubscribeResponseDto} instance.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @PostMapping("/emailNotification/subscribe")
    public ResponseEntity<PlaceSubscribeResponseDto> subscribePlaceEmailNotification(
            @RequestBody PlaceSubscribeDto placeSubscribeDto,
            @Parameter(hidden = true) @CurrentUser UserVO userVO){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.subscribeEmailNotification(placeSubscribeDto, userVO));
    }

    /**
     * Method for unsubscribing email notification about place updates.
     *
     * @return {@link PlaceSubscribeResponseDto} of {@link PlaceSubscribeResponseDto} instance.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PostMapping("/emailNotification/unsubscribe")
    public ResponseEntity<PlaceSubscribeResponseDto> unsubscribePlaceEmailNotification(@Parameter(hidden = true) @CurrentUser UserVO userVO){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.unsubscribeEmailNotification(userVO));
    }

    /**
     * Method update email notification sending frequency.
     *
     * @param placeSubscribeDto of the {@link PlaceSubscribeResponseDto} with EmailNotification.
     * @return {@link PlaceSubscribeResponseDto} of {@link PlaceSubscribeResponseDto} instance.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @PutMapping("/emailNotification/updateFrequency")
    public ResponseEntity<PlaceSubscribeResponseDto> updateEmailNotificationFrequency(
            @RequestBody PlaceSubscribeDto placeSubscribeDto,
            @Parameter(hidden = true) @CurrentUser UserVO userVO
    ){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.updateEmailNotificationFrequency(userVO, placeSubscribeDto.getEmailNotification()));
    }

    @GetMapping("/emailNotification/getAll")
    public EntityResponse getAllPlaceUpdateSubscribers(){
        return null;
    }

    @GetMapping("/emailNotification/getAll/{frequency}")
    public EntityResponse getAllPlaceUpdateSubscribersByFrequency(
            @PathVariable EmailNotification frequency){
        return null;
    }

}
