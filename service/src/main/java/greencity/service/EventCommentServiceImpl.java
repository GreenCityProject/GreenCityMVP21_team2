package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.eventscomment.AddEventCommentDtoRequest;
import greencity.dto.eventscomment.AmountCommentLikesDto;
import greencity.dto.eventscomment.EventCommentDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.notification.TaggedUserNotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.EventComment;
import greencity.entity.Events;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventsRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static greencity.enums.NotificationType.*;

@Service
@EnableCaching
@RequiredArgsConstructor
public class EventCommentServiceImpl implements EventCommentService{
    private final EventsRepo eventsRepo;
    private final EventCommentRepo eventCommentRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    private final TaggedUsersService taggedUsersService;

    @Override
    public AddEventCommentDtoRequest save(AddEventCommentDtoRequest addEventCommentDtoRequest, Long eventId,
                                          UserVO userVO) {
        Events event = eventsRepo.findById(eventId).get();
        EventComment eventComment = modelMapper.map(addEventCommentDtoRequest, EventComment.class);
        eventComment.setEvent(event);
        eventComment.setCreatedDate(ZonedDateTime.now());
        Long parentCommentId = addEventCommentDtoRequest.getEventParentCommentId();
        if (parentCommentId != null && parentCommentId != 0){
            eventComment.setEventParentComment(eventCommentRepo.findById(parentCommentId).get());
            UserVO commentOwner = mapToUserVO(eventComment.getEventParentComment().getAuthor());
            createCommentReplyNotification(userVO,commentOwner,parentCommentId);
        }
        createCommentedEventNotification(userVO,event);
        createTaggedUserNotification(userVO,eventId, addEventCommentDtoRequest.getText());

        return modelMapper.map(eventCommentRepo.save(eventComment), AddEventCommentDtoRequest.class);
    }

    private void createCommentReplyNotification(UserVO userVO, UserVO commentOwner, Long parentCommentId) {
        NotificationDto notification = new NotificationDto(userVO, REPLIED_EVENT_COMMENT, commentOwner, parentCommentId);
        notificationService.createNotification(notification);
    }

    private void createCommentedEventNotification(UserVO userVO, Events event) {
        NotificationDto notification = new NotificationDto(userVO, LIKED_EVENT, mapToUserVO(event.getOrganizer()), event.getId());
        notificationService.createNotification(notification);
    }

    private void createTaggedUserNotification(UserVO userVO, Long eventId, String text) {
        List<UserVO> taggedUsers = taggedUsersService.findTaggedUsersFromText(text);
        TaggedUserNotificationDto notification = new TaggedUserNotificationDto(userVO, TAGGED_USER_UNDER_EVENT ,
                taggedUsers, eventId);
        notificationService.createTaggedUserNotifications(notification);
    }

    @Override
    public void update( Long id, String text, UserVO userVO){
        EventComment eventComment = eventCommentRepo.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (!userVO.getId().equals(eventComment.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        eventComment.setEditingDate(ZonedDateTime.now());
        eventComment.setText(text);
        eventCommentRepo.save(eventComment);
    }

    @Override
    public void delete(Long id, UserVO userVO){
        EventComment eventComment = eventCommentRepo.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (!userVO.getId().equals(eventComment.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        eventCommentRepo.delete(eventComment);
    }

    @Override
    public void like(Long id, UserVO userVO){
        EventComment eventComment = eventCommentRepo.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        Set<User> usersLikedSet = eventComment.getUsersLiked();
        User currentUser = userRepo.findById(userVO.getId()).get();
        if (usersLikedSet.contains(currentUser)){
            usersLikedSet.remove(currentUser);
        } else {
            usersLikedSet.add(currentUser);
            createLikenEventCommentNotification(userVO,id,eventComment.getAuthor());
        }
        eventComment.setUsersLiked(usersLikedSet);
        eventCommentRepo.save(eventComment);
    }

    private void createLikenEventCommentNotification(UserVO userVO, Long id, User receiver) {
        NotificationDto notification = new NotificationDto(userVO,LIKED_EVENT_COMMENT,mapToUserVO(receiver),id);
        notificationService.createNotification(notification);
    }

    @Override
    public Integer getCountOfComments (Long id){
        return eventCommentRepo.countByEventId(id);
    }

    @Override
    public EventCommentDto getEventCommentById(Long id, UserVO userVO){
        EventComment eventComment = eventCommentRepo.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        User currentUser = userRepo.findById(userVO.getId()).get();
        return converterEventCommentToEventCommentDto(eventComment, currentUser);
    }

    @Override
    public AmountCommentLikesDto countLikes (Long id, UserVO userVO){
        EventComment eventComment = eventCommentRepo.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        Set<User> usersLikedSet = eventComment.getUsersLiked();
        User currentUser = userRepo.findById(userVO.getId()).get();
        return AmountCommentLikesDto.builder()
                .id(eventComment.getId())
                .amountLikes(usersLikedSet.size())
                .userId(eventComment.getAuthor().getId())
                .liked(usersLikedSet.contains(currentUser))
                .build();
    }

    @Override
    public PageableDto<EventCommentDto> getAllActiveComments(Pageable page, UserVO userVO, Long eventId){
        User currentUser = userRepo.findById(userVO.getId()).get();
        Page<EventComment> pageEventComment = eventCommentRepo.findAllByEventIdOrderByCreatedDateDesc(
                eventId, page);

        return converterPageEventCommentToPageableDtoEventCommentDto(pageEventComment, currentUser);
    }

    @Override
    public PageableDto<EventCommentDto> findAllActiveReplies(Pageable pageable, UserVO userVO, Long parentCommentId){
        User currentUser = userRepo.findById(userVO.getId()).get();
        Page<EventComment> pageEventComment = eventCommentRepo.
                findAllByEventParentCommentIdOrderByCreatedDateDesc(parentCommentId, pageable);
        return converterPageEventCommentToPageableDtoEventCommentDto(pageEventComment, currentUser);
    }

    @Override
    public Integer getCountOfActiveReplies (Long eventParentCommentId){
        return eventCommentRepo.countByEventParentCommentId(eventParentCommentId);
    }

    private PageableDto<EventCommentDto> converterPageEventCommentToPageableDtoEventCommentDto (
            Page<EventComment> pageEventComment, User currentUser){
        List<EventCommentDto> eventCommentDtos = pageEventComment.stream()
                .map(eventComment -> converterEventCommentToEventCommentDto(eventComment, currentUser))
                .collect(Collectors.toList());

        return new PageableDto<>(
                eventCommentDtos,
                pageEventComment.getTotalElements(),
                pageEventComment.getPageable().getPageNumber(),
                pageEventComment.getTotalPages());
    }

    private EventCommentDto converterEventCommentToEventCommentDto (EventComment eventComment, User currentUser){
        EventCommentDto eventCommentDto = modelMapper.map(eventComment, EventCommentDto.class);
        Set<User> usersLikedSet = eventComment.getUsersLiked();
        eventCommentDto.setCurrentUserLiked(usersLikedSet.contains(currentUser));
        eventCommentDto.setNumberOfLikes(usersLikedSet.size());
        eventCommentDto.setNumberOfReplies(getCountOfActiveReplies(eventComment.getId()));
        return eventCommentDto;
    }

    private UserVO mapToUserVO(User user) {
        return modelMapper.map(user, UserVO.class);
    }
}
