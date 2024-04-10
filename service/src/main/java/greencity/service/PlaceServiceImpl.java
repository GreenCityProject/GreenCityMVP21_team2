package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.place.*;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.EmailNotification;
import greencity.enums.PlaceStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.filters.PlaceSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.CategoryRepo;
import greencity.repository.PlaceRepo;
import greencity.repository.PlaceUpdatesSubscribersRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.*;
import static java.util.Optional.*;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepo placeRepo;
    private final CategoryRepo categoryRepo;
    private final PlaceUpdatesSubscribersRepo placeUpdatesSubscribersRepo;
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

    @Override
    public PageableDto<PlaceInfoDto> filterPlaceBySearchPredicate(Pageable pageable, FilterPlaceDto filterDto){
        Page<Place> placePage = placeRepo.findAll(getSpecification(filterDto), pageable);
        return buildPageableDtoByPlaceInfoDto(placePage);
    }

    private PageableDto<PlaceInfoDto> buildPageableDtoByPlaceInfoDto(Page<Place> placePage){
        List<PlaceInfoDto> placeInfoDtoList = placePage.stream()
                .map(place -> modelMapper.map(place, PlaceInfoDto.class))
                .collect(Collectors.toList());
        return new PageableDto<>(
                placeInfoDtoList,
                placePage.getTotalElements(),
                placePage.getPageable().getPageNumber(),
                placePage.getTotalPages());
    }

    public PlaceSpecification getSpecification(FilterPlaceDto filterDto) {
        List<SearchCriteria> searchCriteria = buildSearchCriteria(filterDto);
        return new PlaceSpecification(searchCriteria);
    }

    public List<SearchCriteria> buildSearchCriteria(FilterPlaceDto filterDto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        if (filterDto.getFilterDiscountDto()!= null) {
            if(filterDto.getFilterDiscountDto().getSpecification() != null
                    && filterDto.getFilterDiscountDto().getSpecification().getName() != null
            ){
                setValueIfNotEmpty(criteriaList, Place_.DISCOUNT_VALUES, Specification_.NAME,
                        filterDto.getFilterDiscountDto().getSpecification().getName());
            }
            criteriaList.add(SearchCriteria.builder()
                    .key(Place_.DISCOUNT_VALUES)
                    .type("discountRange")
                    .value(new String[] {String.valueOf(filterDto.getFilterDiscountDto().getDiscountMin()),
                            String.valueOf(filterDto.getFilterDiscountDto().getDiscountMax())})
                    .build()
            );
        }
        return criteriaList;
    }

    private void setValueIfNotEmpty(List<SearchCriteria> searchCriteria, String key, String type, String value) {
        if (!StringUtils.isEmpty(value)) {
            searchCriteria.add(SearchCriteria.builder()
                    .key(key)
                    .type(type)
                    .value(value)
                    .build());
        }
    }

    @Override
    public PlaceSubscribeResponseDto subscribeEmailNotification(PlaceSubscribeDto placeSubscribeDto, UserVO userVO) {
        Optional<PlaceUpdatesSubscribers> optionalPlaceUpdatesSubscribers = placeUpdatesSubscribersRepo.findByUserId(userVO.getId());
        if(optionalPlaceUpdatesSubscribers.isPresent()) throw new BadRequestException("User is already subscribed place updates");
        placeUpdatesSubscribersRepo.save(
                new PlaceUpdatesSubscribers(null, modelMapper.map(userVO,User.class), placeSubscribeDto.getEmailNotification()));
        return new PlaceSubscribeResponseDto(placeSubscribeDto.getEmailNotification(), userVO.getId());
    }


    @Override
    @Transactional
    public PlaceSubscribeResponseDto unsubscribeEmailNotification(UserVO userVO) {
        Optional<PlaceUpdatesSubscribers> optionalPlaceUpdatesSubscribers = placeUpdatesSubscribersRepo.findByUserId(userVO.getId());
        if (optionalPlaceUpdatesSubscribers.isEmpty()) throw new BadRequestException("User isn't subscribed place updates");
        placeUpdatesSubscribersRepo.deleteByUserId(userVO.getId());
        return new PlaceSubscribeResponseDto(EmailNotification.DISABLED, userVO.getId());
    }

    @Override
    public PlaceSubscribeResponseDto updateEmailNotificationFrequency(UserVO userVO, EmailNotification  emailNotification){
        Optional<PlaceUpdatesSubscribers> optionalPlaceUpdatesSubscribers = placeUpdatesSubscribersRepo.findByUserId(userVO.getId());
        if (optionalPlaceUpdatesSubscribers.isEmpty()) throw new BadRequestException("User isn't subscribed place updates");
        PlaceUpdatesSubscribers placeUpdatesSubscribers = optionalPlaceUpdatesSubscribers.get();
        if (placeUpdatesSubscribers.getEmailNotification() == emailNotification)
            throw new BadRequestException("Nothing to update. Current frequency: " + emailNotification.toString());
        placeUpdatesSubscribersRepo.updateEmailNotificationByUserId(userVO.getId(), emailNotification);
        return new PlaceSubscribeResponseDto(emailNotification, userVO.getId());
    }
}
