package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.place.AddPlaceDto;
import greencity.dto.place.OpeningHoursDto;
import greencity.dto.place.PlaceResponse;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.PlaceStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CategoryRepo;
import greencity.repository.PlaceRepo;
import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlaceUpdateDto;
import greencity.repository.PlaceRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.*;
import static java.util.Optional.*;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
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
    public PlaceInfoDto getInfo(Long id){
        return modelMapper.map(placeRepo.findById(id), PlaceInfoDto.class);
    }

    @Override
    public PlaceUpdateDto getPlaceById(Long id){
        return modelMapper.map(placeRepo.findById(id), PlaceUpdateDto.class);
    }
}
