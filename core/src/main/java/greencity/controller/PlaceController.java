package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.PageableDto;
import greencity.dto.place.AddPlaceDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.FilterPlaceDto;
import greencity.dto.place.PlaceResponse;
import greencity.dto.user.UserVO;
import greencity.enums.EmailNotification;
import greencity.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlaceUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
                                   @io.swagger.v3.oas.annotations.parameters.
                                           RequestBody(description = SwaggerExampleModel.ADD_PLACE_DTO)
                                   @RequestBody @Validated AddPlaceDto addPlace){
        return placeService.createPlace(user,addPlace);
    }

    @Operation(summary = "Filter places")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/filter/predicate")
    @ApiPageable
    public PageableDto<AdminPlaceDto> filterPlace(
            @Valid @RequestBody FilterPlaceDto filterDto, @Parameter(hidden = true) Pageable pageable) {
        return placeService.filterPlaces(filterDto, pageable);
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
        return ResponseEntity.status(HttpStatus.OK)
                .body(placeService.updateEmailNotificationFrequency(userVO, placeSubscribeDto.getEmailNotification()));
    }

    /**
     * Method return list of place updates subscribers
     *
     * @return {@link PlaceSubscribeResponseDto} of {@link PlaceSubscribeResponseDto} instance.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/emailNotification/getAllSubscribers")
    public ResponseEntity<List<PlaceSubscribeResponseDto>> getAllPlaceUpdateSubscribers(){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.getAllPlaceUpdatesSubscribers());
    }

    /**
     * Method return list of place updates subscribers by frequency.
     *
     * @param frequency of the {@link EmailNotification}.
     * @return {@link PlaceSubscribeResponseDto} of {@link PlaceSubscribeResponseDto} instance.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/emailNotification/getAllSubscribers/{frequency}")
    public ResponseEntity<List<PlaceSubscribeResponseDto>> getAllPlaceUpdateSubscribersByFrequency(
            @PathVariable EmailNotification frequency){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.getAllPlaceUpdatesSubscribersByFrequency(frequency));
    }
}
