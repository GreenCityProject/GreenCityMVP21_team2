package greencity.service;

import greencity.client.RestClient;
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
import greencity.repository.EventsRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableCaching
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventsRepo eventsRepo;
    private final RestClient restClient;
    private final ModelMapper modelMapper;
    private final TagsService tagService;
    private final FileService fileService;

    @Override
    public EventDto save(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, Long userId) {
        EventDto eventsDto = modelMapper.map(addEventDtoRequest, EventDto.class);

        User user = modelMapper.map(restClient.findById(userId), User.class);
        eventsDto.setOrganizer(modelMapper.map(user, EventAuthorDto.class));

        ZonedDateTime date = ZonedDateTime.now();
        eventsDto.setCreationDate(date.toString());

        List<TagVO> tagVOS = tagService.findTagsByNamesAndType(
                addEventDtoRequest.getTags(), TagType.EVENT);

        eventsDto.setTags(converterListTagVOToListTagUaEnDto(tagVOS));

        if (eventsDto.getOpen() == null) {
            eventsDto.setOpen(true);
        }
        setImagesInEventDto(eventsDto, images);
        eventsDto.setDates(addEventDtoRequest.getDatesLocations());
        Events events = modelMapper.map(eventsDto, Events.class);
        Events finalEvent = converterEventDtoToEvent(eventsDto, events);
        try {
            eventsRepo.save(finalEvent);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
        return eventsDto;
    }

    @Override
    public PageableAdvancedDto<EventDto> findAll(Pageable page) {
        Page<Events> pages;
        pages = eventsRepo.findAll(page);
        return null;

    }

    @Override
    public EventVO findById(Long id) {
        Events events = eventsRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENTS_NOT_FOUND_BY_ID + id));
        return modelMapper.map(events, EventVO.class);
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
    public EventDto update(EventDto eventDto, List<MultipartFile> images, Long userId) {
        setImagesInEventDto(eventDto, images);
        Events events = modelMapper.map(findById(eventDto.getId()), Events.class);
        Events finalEvent = converterEventDtoToEvent(eventDto, events);
        Events eventForTag = modelMapper.map(eventDto, Events.class);

        finalEvent.setTags(eventForTag.getTags());
        finalEvent.setOpen(eventForTag.getOpen());

        try {
            eventsRepo.save(finalEvent);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
        return eventDto;
    }

    @Override
    public void rate(Long id, int grade) {

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
            eventDto.setTitleImage("some image");
        }
    }

    private Events converterEventDtoToEvent (EventDto eventDto, Events events){
        events.setCreationDate(ZonedDateTime.parse(eventDto.getCreationDate()));
        events.setDatesLocations(converterListEventDateLocationDtoToListEventDateLocation(
                eventDto.getDates(), events));
        events.setEventsImages(converterListStringToListEventsImages(eventDto.getAdditionalImages(), events));
        return events;
    }
}
