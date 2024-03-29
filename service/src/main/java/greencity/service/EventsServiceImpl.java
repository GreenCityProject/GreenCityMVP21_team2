package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.events.*;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EventDateLocationsRepo;
import greencity.repository.EventsImagesRepo;
import greencity.repository.EventsRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@EnableCaching
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventsRepo eventsRepo;
    private final EventDateLocationsRepo eventDateLocationsRepo;
    private final EventsImagesRepo eventsImagesRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final TagsService tagService;
    private final FileService fileService;
    private int maxImagesListSize = 6;
    private int maxDateLocationListSize = 7;
    private String defaultImage = "https://csb10032000a548f571" +
            ".blob.core.windows.net/allfiles/e95283db-3f71-4867-95cd-e28bf0a6afebillustration-earth.png";

    @Override
    public EventDto save(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, Long userId) {
        List<EventDateLocationDto> eventDateLocationDtos = addEventDtoRequest.getDatesLocations();
        checkEvent(eventDateLocationDtos, images);
        EventDto eventsDto = modelMapper.map(addEventDtoRequest, EventDto.class);

        User user = userRepo.getById(userId);
        eventsDto.setOrganizer(modelMapper.map(user, EventAuthorDto.class));

        eventsDto.setCreationDate(ZonedDateTime.now().toString());

        List<TagVO> tagVOS = tagService.findTagsByNamesAndType(
                addEventDtoRequest.getTags(), TagType.EVENT);

        if (eventsDto.getOpen() == null) {
            eventsDto.setOpen(true);
        }
        setImagesInEventDto(eventsDto, images);
        eventsDto.setDates(eventDateLocationDtos);
        Events events = modelMapper.map(eventsDto, Events.class);
        Events finalEvent = converterEventDtoToEvent(eventsDto, events);
        finalEvent.setCreationDate(ZonedDateTime.parse(eventsDto.getCreationDate()));
        finalEvent.setTags(tagVOS.stream().map(tagVO -> modelMapper.map(tagVO, Tag.class)).toList());
        try {
            eventsRepo.save(finalEvent);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
        return eventsDto;
    }

    @Override
    public PageableAdvancedDto<EventDto> findAll(Pageable page, Long userId) {
        User user = userRepo.getById(userId);
        Page<Events> pages = eventsRepo.findAllByOrderByCreationDateDesc(page);
        return buildPageableAdvancedDtoByEventDto(pages, user);

    }

    @Override
    public PageableAdvancedDto<EventDto> findAllEventsCreatedByUser(Pageable page, UserVO userVO) {
        User user = userRepo.getById(userVO.getId());
        Page<Events> pages = eventsRepo.findAllByOrganizerOrderByCreationDateDesc(user, page);
        return buildPageableAdvancedDtoByEventDto(pages, user);
    }

    @Override
    public  PageableAdvancedDto<EventDto> findAllRelatedToUserEvents(UserVO userVO, Pageable page){
        User user = userRepo.getById(userVO.getId());
        Page<Events> pages = eventsRepo.findAllByOrganizerOrEventAttenderOrderByCreationDateDesc(
                user.getId(), page);
        return buildPageableAdvancedDtoByEventDto(pages, user);
    }

    @Override
    public PageableAdvancedDto<EventDto> findAllUserEvents(UserVO userVO, Pageable page){
        User user = userRepo.getById(userVO.getId());
        Page<Events> pages = eventsRepo.findAllByEventAttenderOrderByCreationDateDesc(
                user.getId(), page);
        return buildPageableAdvancedDtoByEventDto(pages, user);
    }

    @Override
    public EventVO findById(Long id) {
        Events events = eventsRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENTS_NOT_FOUND_BY_ID + id));
        return modelMapper.map(events, EventVO.class);
    }

    @Override
    public EventDto findEventById(Long id, Long userId) {
        Events events = eventsRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENTS_NOT_FOUND_BY_ID + id));
        User user = userRepo.getById(userId);
        return converterEventToEventDto(events, user);
    }

    @Override
    public void delete(Long id, UserVO user) {
        EventVO eventVO = findById(id);
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(eventVO.getOrganizer().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        eventsRepo.deleteById(id);
    }

    @Override
    public EventDto update(EventDtoYoUpdate eventDtoYoUpdate, List<MultipartFile> images,  UserVO user) {
        EventDto eventDto = converterEventDtoYoUpdateToEventDto(eventDtoYoUpdate);
        Long eventId = eventDto.getId();
        Events events = eventsRepo.findById(eventId).get();
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(events.getOrganizer().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        checkEvent(eventDto.getDates(), images);
        eventDateLocationsRepo.deleteAllEventDateLocationsByEventId(eventId);
        eventDtoYoUpdate.getImagesToDelete().stream().forEach(link ->
                eventsImagesRepo.deleteAllEventsImagesByEventIdAndLink(eventId, link));
        setImagesInEventDto(eventDto, images);
        Events finalEvent = converterEventDtoToEvent(eventDto, events);
        finalEvent.setTitle(eventDto.getTitle());
        if(eventDtoYoUpdate.getImagesToDelete().contains(events.getTitleImage())) {
            finalEvent.setTitleImage(eventDto.getTitleImage());
        } else {
            eventDto.setTitleImage(events.getTitleImage());
        }
        finalEvent.setDescription(eventDto.getDescription());
        finalEvent.setTags(events.getTags());
        finalEvent.setOpen(events.getOpen());
        try {
            eventsRepo.save(finalEvent);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
        return eventDto;
    }

    @Override
    public void addAttender (Long id, UserVO userVO){
        Events event = eventsRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENTS_NOT_FOUND_BY_ID + id));
        User user = userRepo.getById(userVO.getId());
        Set<User> userSet = new HashSet<>(event.getEventAttender());
        userSet.add(user);
        event.setEventAttender(userSet);
        try {
            eventsRepo.save(event);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
    }

    @Override
    public void addToFavorites (Long id, UserVO userVO){
        Events event = eventsRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENTS_NOT_FOUND_BY_ID + id));
        User user = userRepo.getById(userVO.getId());
        Set<User> userSet = new HashSet<>(event.getEventsFollowers());
        userSet.add(user);
        event.setEventsFollowers(userSet);
        try {
            eventsRepo.save(event);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
    }

    @Override
    public void removeAttender (Long id, UserVO userVO){
        Events event = eventsRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENTS_NOT_FOUND_BY_ID + id));
        User user = userRepo.getById(userVO.getId());
        Set<User> userSet = new HashSet<>(event.getEventAttender());
        userSet.remove(user);
        event.setEventAttender(userSet);
        try {
            eventsRepo.save(event);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
    }

    @Override
    public void removeFromFavorites (Long id, UserVO userVO){
        Events event = eventsRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENTS_NOT_FOUND_BY_ID + id));
        User user = userRepo.getById(userVO.getId());
        Set<User> userSet = new HashSet<>(event.getEventsFollowers());
        userSet.remove(user);
        event.setEventsFollowers(userSet);
        try {
            eventsRepo.save(event);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
    }

    @Override
    public List<EventAttenderDto> findAllEventAttenders(Long id){
        Events event = eventsRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENTS_NOT_FOUND_BY_ID + id));
        return event.getEventAttender().stream().map(user -> modelMapper.map(user, EventAttenderDto.class)).toList();
    }

    @Override
    public void rateEvent(Long eventId, int grade){
        if (grade < 1 || grade > 100) {
            throw new IllegalArgumentException("Grade should be between 1 and 100");
        }
        Events event = eventsRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENTS_NOT_FOUND_BY_ID + eventId));
        double newRating;
        if (event.getRating() == null){
            newRating = grade;
        } else {
            double currentRating = event.getRating();
            double totalRatings = currentRating + 1;
            newRating = ((currentRating * totalRatings) + grade) / (totalRatings + 1);
        }
        event.setRating(newRating);

        eventsRepo.save(event);
    }

    private String extractFromTagVONameInLanguage (List<TagVO> tagVOS, String language) {
        return String.valueOf(tagVOS.stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .filter(t -> t.getLanguageVO().getCode().equals(language))
                .map(TagTranslationVO::getName)
                .collect(Collectors.toList()));
    }

    private List<TagUaEnDto> converterListTagVOToListTagUaEnDto (List<TagVO> tagsVO){
        return tagsVO.stream()
                .map(tagVO -> TagUaEnDto.builder()
                        .id(tagVO.getId())
                        .nameEn(extractFromTagVONameInLanguage(tagsVO, "en"))
                        .nameUa(extractFromTagVONameInLanguage(tagsVO,"ua"))
                        .build())
                .collect(Collectors.toList());
    }

    private List<EventDateLocation> converterListEventDateLocationDtoToListEventDateLocation
            (List<EventDateLocationDto> eventDateLocationDtos, Events events){
        return eventDateLocationDtos.stream()
                .map(eventDateLocationDto -> EventDateLocation.builder()
                        .event(events)
                        .startDate(eventDateLocationDto.getStartDate())
                        .finishDate(eventDateLocationDto.getFinishDate())
                        .latitude(eventDateLocationDto.getCoordinates().getLatitude())
                        .longitude(eventDateLocationDto.getCoordinates().getLongitude())
                        .onlineLink(eventDateLocationDto.getOnlineLink())
                        .build())
                .collect(Collectors.toList());
    }

    private List<EventDateLocationDto> converterListEventDateLocationToListEventDateLocationDto(List<EventDateLocation> eventDateLocations){
        return eventDateLocations.stream()
                .map(this::convertToEventDateLocationDto)
                .collect(Collectors.toList());
    }

    private EventDateLocationDto convertToEventDateLocationDto(EventDateLocation eventDateLocation) {
        return EventDateLocationDto.builder()
                .id(eventDateLocation.getId())
                .event(modelMapper.map(eventDateLocation.getEvent(), EventDto.class))
                .startDate(eventDateLocation.getStartDate())
                .finishDate(eventDateLocation.getFinishDate())
                .onlineLink(eventDateLocation.getOnlineLink())
                .coordinates(AddressDto.builder()
                        .latitude(eventDateLocation.getLatitude())
                        .longitude(eventDateLocation.getLongitude())
                        .build())
                .build();
    }

    private List<EventsImages> converterListStringToListEventsImages (List<String> list, Events events){
        if( list != null) {
            return list.stream()
                    .map(link -> EventsImages.builder()
                            .link(link)
                            .event(events)
                            .build())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private void setImagesInEventDto (EventDto eventDto, List<MultipartFile> images){
        if (images != null) {
            eventDto.setTitleImage(fileService.upload(images.getFirst()));
            images.removeFirst();
            eventDto.setAdditionalImages(images.stream()
                    .map(fileService::upload)
                    .collect(Collectors.toList()));
        } else {
            eventDto.setTitleImage(defaultImage);
        }
    }

    private Events converterEventDtoToEvent (EventDto eventDto, Events event){
        event.setDatesLocations(converterListEventDateLocationDtoToListEventDateLocation(
                eventDto.getDates(), event));
        event.setEventsImages(converterListStringToListEventsImages(eventDto.getAdditionalImages(), event));
        return event;
    }

    private EventDto converterEventToEventDto (Events event, User user){
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setAdditionalImages(event.getEventsImages().stream().map(EventsImages::getLink).collect(Collectors.toList()));
        List<TagVO> tagVOS = event.getTags().stream().map(tag ->  tagService.findById(
                tag.getId())).collect(Collectors.toList());
        eventDto.setTags(converterListTagVOToListTagUaEnDto(tagVOS));
        eventDto.setDates(converterListEventDateLocationToListEventDateLocationDto(event.getDatesLocations()));
        eventDto.setIsSubscribed(event.getEventAttender().contains(user));
        eventDto.setIsFavorite(event.getEventsFollowers().contains(user));
        return eventDto;
    }

    private EventDto converterEventDtoYoUpdateToEventDto (EventDtoYoUpdate eventDtoYoUpdate){
        List<TagVO> tagVOS = tagService.findTagsByNamesAndType(
                eventDtoYoUpdate.getTags(), TagType.EVENT);
        return EventDto.builder()
                .id(eventDtoYoUpdate.getId())
                .title(eventDtoYoUpdate.getTitle())
                .titleImage(eventDtoYoUpdate.getTitleImage())
                .tags(converterListTagVOToListTagUaEnDto(tagVOS))
                .organizer(eventDtoYoUpdate.getOrganizer())
                .open(eventDtoYoUpdate.getOpen())
                .description(eventDtoYoUpdate.getDescription())
                .dates(eventDtoYoUpdate.getDatesLocations())
                .creationDate(eventDtoYoUpdate.getCreationDate())
                .additionalImages(eventDtoYoUpdate.getAdditionalImages())
                .build();
    }

    private PageableAdvancedDto<EventDto> buildPageableAdvancedDtoByEventDto(Page<Events> eventsPage, User user) {
        List<EventDto> eventsDtos = eventsPage.stream()
                .map(event -> converterEventToEventDto(event, user))
                .collect(Collectors.toList());

        return new PageableAdvancedDto<>(
                eventsDtos,
                eventsPage.getTotalElements(),
                eventsPage.getPageable().getPageNumber(),
                eventsPage.getTotalPages(),
                eventsPage.getNumber(),
                eventsPage.hasPrevious(),
                eventsPage.hasNext(),
                eventsPage.isFirst(),
                eventsPage.isLast());
    }

    private void checkEvent (List<EventDateLocationDto> eventDateLocationDtos, List<MultipartFile> images){
        HashSet<ZonedDateTime> dateSet = new HashSet<>();
        if (eventDateLocationDtos.stream().anyMatch(eventDateLocationDto ->
                eventDateLocationDto.getStartDate().isBefore(ZonedDateTime.now()))){
            throw new NotSavedException(ErrorMessage.EVENT_DATE_GREATER_CURRENT_DATE);
        }
        if (((images!= null)&&(images.size() > maxImagesListSize)) ||
                (eventDateLocationDtos.size() > maxDateLocationListSize)){
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
        if (eventDateLocationDtos.stream()
                .anyMatch(eventDateLocationDto -> !dateSet.add(eventDateLocationDto.getStartDate()) ||
                        !dateSet.add(eventDateLocationDto.getFinishDate()))){
            throw new NotSavedException(ErrorMessage.EVENT_SAME_DATE);
        }
    }
}
