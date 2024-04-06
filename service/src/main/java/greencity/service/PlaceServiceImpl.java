package greencity.service;

import greencity.builder.PageableAdvancedBuilder;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterDistanceDto;
import greencity.dto.place.*;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.PlaceStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.filters.*;
import greencity.repository.CategoryRepo;
import greencity.repository.PlaceRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.*;
import static greencity.filters.GeneralPlaceSpecification.*;
import static java.lang.Math.*;
import static java.util.Arrays.*;
import static java.util.Objects.*;
import static java.util.Optional.*;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    private static final double EARTH_RADIUS = 6371.0;

    private final PlaceRepo placeRepo;
    private final CategoryRepo categoryRepo;
    private final GeocodingService geocodingService;
    private final ModelMapper modelMapper;

    private final String defaultImage = "default";

    @Override
    public PlaceResponse createPlace(UserVO user, AddPlaceDto addPlace) {
        var category = getCategory(addPlace.getCategoryName());
        var placeLocation = createLocation(addPlace.getLocationName());
        var openingHoursList = mapListOfHoursDtoToHours(addPlace.getOpeningHoursList());

        var place = createPlace(addPlace.getPlaceName());
        setCategoryToPlace(place,category);
        setLocationToPlace(place,placeLocation);
        setOpeningHoursToPlace(place,openingHoursList);
        setAuthorToPlace(place,user);

        return savePlace(place);
    }

    private Category getCategory(String categoryName) {
        return of(categoryRepo.findCategoryByName(categoryName))
                .orElseThrow(()-> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND_BY_NAME_OR_NAME_UA + categoryName));
    }

    private PlaceLocations createLocation(String locationName) {
        var enGeocode = geocodingService.getGeocodingResultsFromAddress(locationName, ENGLISH_GOOGLE_LANG_CODE)[0];
        var uaGeocode = geocodingService.getGeocodingResultsFromAddress(locationName, UKRAINIAN_GOOGLE_LANG_CODE)[0];

        return PlaceLocations.builder()
                .address(enGeocode.formattedAddress)
                .addressUa(uaGeocode.formattedAddress)
                .lat(enGeocode.geometry.location.lat)
                .lng(enGeocode.geometry.location.lng)
                .build();
    }

    private List<OpeningHours> mapListOfHoursDtoToHours(List<OpeningHoursDto> dtos) {
        return dtos.stream()
                .map(h -> modelMapper.map(h, OpeningHours.class))
                .collect(Collectors.toList());
    }

    private Place createPlace(String placeName) {
        return new Place(placeName, defaultImage, PlaceStatus.APPROVED);
    }

    private void setCategoryToPlace(Place place, Category category) {
        place.setCategory(category);
    }

    private void setLocationToPlace(Place place, PlaceLocations location) {
        place.setLocation(location);
    }

    private void setOpeningHoursToPlace(Place place, List<OpeningHours> openingHoursList) {
        place.addOpeningHours(openingHoursList);
    }

    private void setAuthorToPlace(Place place, UserVO user) {
        place.setAuthor(modelMapper.map(user,User.class));
    }

    private PlaceResponse savePlace(Place place) {
        return modelMapper.map(placeRepo.save(place), PlaceResponse.class);
    }


    @Override
    public PageableDto<AdminPlaceDto> filterPlaces(FilterPlaceDto filterPlaceDto, Pageable pageable) {
        var optionalSpecification = createPlaceSpecification(filterPlaceDto);
        var placePage = findPlaces(optionalSpecification, pageable);
        var places = findPlacesInRadius(placePage.getContent(), filterPlaceDto);
        var adminPlaceDtoList = mapToAdminPlaceDtoList(places);

        return PageableAdvancedBuilder.getPageableDto(adminPlaceDtoList, placePage);
    }

    private List<AdminPlaceDto> mapToAdminPlaceDtoList(List<Place> places) {
        return places.stream()
                .map(p -> modelMapper.map(p, AdminPlaceDto.class))
                .toList();
    }

    private Optional<Specification<Place>> createPlaceSpecification(FilterPlaceDto filter) {
        if (isNull(filter))
            return empty();

        var categorySpecific = new PlaceCategorySpecification(filter.getCategories());
        var hoursSpecific = new OpeningHoursPlaceSpecification(filter.getTime());
        var discountSpecific = new PlaceDiscountSpecification(filter.getFilterDiscountDto());
        var locationSpecific = new PlaceLocationSpecification(filter.getMapBoundsDto());
        var regexSpecific = new PlaceQueryStatusSpecification(filter.getSearchReg(),filter.getPlaceStatus());

        var specifications = asList(categorySpecific,hoursSpecific,discountSpecific,locationSpecific,regexSpecific);
        return of(andAll(specifications));
    }

    private Page<Place> findPlaces(Optional<Specification<Place>> specification, Pageable pageable) {
        return specification.map(s -> placeRepo.findAll(s,pageable))
                .orElseGet(()-> placeRepo.findAll(pageable));
    }

    private List<Place> findPlacesInRadius(List<Place> places, FilterPlaceDto filter) {
        return isNull(filter) || isNull(filter.getFilterDistanceDto()) ?
            places : filterByDistanceToUser(places,filter.getFilterDistanceDto());
    }

    private List<Place> filterByDistanceToUser(List<Place> places, FilterDistanceDto filterDistance) {
        return places.stream()
                .filter(place -> isPlaceInRadius(filterDistance, place))
                .toList();
    }

    private boolean isPlaceInRadius(FilterDistanceDto filterDistance, Place place) {
        return calculateDistance(place.getLocation(), filterDistance) < filterDistance.getDistance();
    }

    public double calculateDistance(PlaceLocations location, FilterDistanceDto filterDistance) {
        var placeLatRad = toRadians(location.getLat());
        var placeLngRad = toRadians(location.getLng());
        var userLatRad = toRadians(filterDistance.getLat());
        var userLngRad = toRadians(filterDistance.getLng());

        var deltaLat = userLatRad - placeLatRad;
        var deltaLng = userLngRad - placeLngRad;

        var a = pow(sin(deltaLat / 2), 2) + cos(placeLatRad) * cos(userLatRad) * pow(sin(deltaLng / 2), 2);
        return EARTH_RADIUS * 2 * atan2(sqrt(a), sqrt(1 - a));
    }
}
