package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.events.*;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.tag.TagVO;
import greencity.entity.*;
import greencity.enums.TagType;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EventsRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableCaching
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService{

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

        List<TagUaEnDto> tagsUaEnDto = tagVOS.stream()
                .map(tagVO -> TagUaEnDto.builder()
                        .id(tagVO.getId())
                        .nameEn(extractFromTagVONameInLanguage(tagVOS, "en"))
                        .nameUa(extractFromTagVONameInLanguage(tagVOS,"ua"))
                        .build())
                .collect(Collectors.toList());

        eventsDto.setTags(tagsUaEnDto);

        if (eventsDto.getOpen() == null) {
            eventsDto.setOpen(true);
        }
        MultipartFile titleImage = images.getFirst();
        if (titleImage != null) {
            eventsDto.setTitleImage(fileService.upload(titleImage));
        }
        if(eventsDto.getTitleImage() == null){
            eventsDto.setTitleImage("some image");
        }
        if (!images.isEmpty()) {
            images.removeFirst();
            eventsDto.setAdditionalImages(images.stream()
                    .map(fileService::upload)
                    .collect(Collectors.toList()));
        }

        eventsDto.setDates(addEventDtoRequest.getDatesLocations());

        Events events = modelMapper.map(eventsDto, Events.class);
        events.setCreationDate(date);
        events.setDatesLocations(addEventDtoRequest.getDatesLocations().stream()
                .map(eventDateLocationDto -> EventDateLocation.builder()
                        .event(events)
                        .startDate(eventDateLocationDto.getStartDate())
                        .finishDate(eventDateLocationDto.getFinishDate())
                        .latitude(eventDateLocationDto.getCoordinates().getLatitude())
                        .longitude(eventDateLocationDto.getCoordinates().getLongitude())
                        .onlineLink(eventDateLocationDto.getOnlineLink())
                        .build())
                .collect(Collectors.toList()));
        events.setEventsImages(eventsDto.getAdditionalImages().stream()
                        .map(link -> EventsImages.builder()
                                .link(link)
                                .event(events)
                                .build())
                .collect(Collectors.toList()));
        try {
            eventsRepo.save(events);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
        return eventsDto;
    }

    @Override
    public PageableAdvancedDto<EventDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public EventVO findById(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public EventDto update(AddEventDtoRequest addEventDtoRequest, MultipartFile image) {
        return null;
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
}
