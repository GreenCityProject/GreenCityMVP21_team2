package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.events.AddEventDtoRequest;
import greencity.dto.events.EventAuthorDto;
import greencity.dto.events.EventDto;
import greencity.dto.events.EventVO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.tag.TagVO;
import greencity.entity.Events;
import greencity.entity.Tag;
import greencity.entity.User;
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
    public EventDto save(AddEventDtoRequest addEventDtoRequest, MultipartFile image, Long userId) {
        Events events = modelMapper.map(addEventDtoRequest, Events.class);
        EventDto eventsDto = modelMapper.map(addEventDtoRequest, EventDto.class);
        User user = modelMapper.map(restClient.findById(userId), User.class);
        events.setOrganizer(user);
        eventsDto.setOrganizer(modelMapper.map(user, EventAuthorDto.class));
        ZonedDateTime date = ZonedDateTime.now();
        events.setCreationDate(date);
        eventsDto.setCreationDate(date.toString());
        if (events.getTags().size() < addEventDtoRequest.getTags().size()) {
            throw new NotSavedException(ErrorMessage.EVENTS_NOT_SAVED);
        }
        List<TagVO> tagVOS = tagService.findTagsByNamesAndType(
                addEventDtoRequest.getTags(), TagType.EVENT);

        List<Tag> tags = tagVOS.stream()
                .map(tagVO -> modelMapper.map(tagVO, Tag.class))
                .collect(Collectors.toList());
        tagVOS.forEach(System.out::println);
        List<TagUaEnDto> tagsUaEnDto = tagVOS.stream()
                .map(tagVO -> TagUaEnDto.builder()
                        .id(tagVO.getId())
                        .nameEn(String.valueOf(tagVOS.stream()
                                .flatMap(t -> t.getTagTranslations().stream())
                                    .filter(t -> t.getLanguageVO().getCode().equals("en"))
                                    .map(TagTranslationVO::getName)
                                    .collect(Collectors.toList())))
                        .nameUa(String.valueOf(tagVOS.stream()
                                .flatMap(t -> t.getTagTranslations().stream())
                                .filter(t -> t.getLanguageVO().getCode().equals("ua"))
                                .map(TagTranslationVO::getName)
                                .collect(Collectors.toList())))
                        .build())
                .collect(Collectors.toList());

        events.setTags(tags);
        eventsDto.setTags(tagsUaEnDto);

        if (events.getOpen() == null) {
            events.setOpen(true);
        }
        if (image != null) {
            events.setTitleImage(fileService.upload(image));
        }
        if(events.getTitleImage() == null){
            events.setTitleImage("some image");
        }
        eventsDto.setTitleImage(events.getTitleImage());
        eventsDto.setDates(addEventDtoRequest.getDatesLocations());
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
}
