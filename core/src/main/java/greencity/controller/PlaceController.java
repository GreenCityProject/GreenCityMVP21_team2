package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.place.AddPlaceDto;
import greencity.dto.place.PlaceResponse;
import greencity.dto.user.UserVO;
import greencity.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlaceUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @Operation(summary = "propose Cafe Place")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @PostMapping("/v2/cafe/save")
    public PlaceResponse proposeCafePlace(@Parameter(hidden = true) @CurrentUser UserVO user,
                                   @RequestBody @Validated AddPlaceDto addPlace){
        return placeService.proposedCafePlace(user,addPlace);
    }
}
