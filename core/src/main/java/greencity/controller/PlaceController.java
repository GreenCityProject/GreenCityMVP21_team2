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
import greencity.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
}