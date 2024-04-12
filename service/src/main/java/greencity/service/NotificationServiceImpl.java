package greencity.service;

import static greencity.constant.ErrorMessage.NOTIFICATION_NOT_FOUND_BY_ID;
import static greencity.constant.ErrorMessage.USER_HAS_NO_PERMISSION;
import static greencity.enums.NotificationStatus.READ;
import static greencity.enums.NotificationStatus.UNREAD;
import static java.time.LocalDateTime.*;

import greencity.builder.PageableAdvancedBuilder;
import greencity.constant.AppConstant;
import greencity.dto.CreatNotificationDto;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationStatus;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<NotificationDto> getAllNotifications(Long receiverId) {
        var notifications = getNotifications(receiverId,Pageable.unpaged());
        return getPageableNotifications(notifications).getPage();
    }

    private Page<Notification> getNotifications(Long receiverId, Pageable pageable) {
        return notificationRepo.findAllByReceiverId(receiverId, pageable);
    }


    @Override
    public List<NotificationDto> getLatestUnreadNotifications(Long receiverId) {
        var notifications = getUnreadNotificationTranslations(receiverId, getLatestPageable());
        return getPageableNotifications(notifications).getPage();
    }

    private Page<Notification> getUnreadNotificationTranslations(Long receiverId, Pageable pageable) {
        return notificationRepo.findAllUnreadByReceiverId(receiverId, pageable);
    }

    private PageRequest getLatestPageable() {
        return PageRequest.ofSize(AppConstant.LATEST_NOTIFICATION_SIZE)
                .withSort(Sort.by(AppConstant.LATEST_NOTIFICATION_SORT_FIELD).descending());
    }

    private PageableAdvancedDto<NotificationDto> getPageableNotifications(Page<Notification> notifications) {
        var notificationDtos = mapToNotificationDtos(notifications.getContent());
        return PageableAdvancedBuilder.getPageableAdvancedDto(notificationDtos, notifications);
    }

    private List<NotificationDto> mapToNotificationDtos(List<Notification> translations) {
        return translations.stream()
                .map(this::mapToNotificationDto)
                .toList();
    }

    private NotificationDto mapToNotificationDto(Notification notification) {
        return modelMapper.map(notification, NotificationDto.class);
    }


    @Override
    @Transactional
    public void removeNotificationById(UserVO user, Long id) {
        var notification = getNotificationWithUserById(id);
        checkUserHasPermissionToAccess(user, notification);
        removeNotification(notification);
    }

    private void removeNotification(Notification notification) {
        notificationRepo.delete(notification);
    }


    @Override
    @Transactional
    public NotificationDto changeStatusToRead(Long id, UserVO user) {
        var notification = getNotificationWithUserById(id);
        checkUserHasPermissionToAccess(user,notification);
        decreaseActiveNotificationTime(notification);
        changeStatus(notification, READ);

        return mapToNotificationDto(notification);
    }

    @Override
    @Transactional
    public NotificationDto changeStatusToUnread(Long id, UserVO user) {
        var notification = getNotificationWithUserById(id);
        checkUserHasPermissionToAccess(user,notification);
        changeStatus(notification,UNREAD);

        return mapToNotificationDto(notification);
    }

    private void changeStatus(Notification notification, NotificationStatus status){
        notification.setStatus(status);
    }

    private void decreaseActiveNotificationTime(Notification notification) {
        if (notification.getStatus().equals(UNREAD)){
            var newExpirationTime = now().plusDays(AppConstant.READ_NOTIFIC_ACTIVE_DAYS_TIME);
            if(notification.getExpireAt().isAfter(newExpirationTime))
                notification.setExpireAt(newExpirationTime);
        }
    }

    private Notification getNotificationWithUserById(Long id) {
        return notificationRepo.findByIdFetchUser(id)
                .orElseThrow(() -> new NotFoundException(NOTIFICATION_NOT_FOUND_BY_ID + id));
    }

    private void checkUserHasPermissionToAccess(UserVO user, Notification notification) {
        var receiverId = notification.getReceiver().getId();

        if ((!user.getRole().equals(Role.ROLE_ADMIN)) && (!user.getId().equals(receiverId))) {
            throw new UserHasNoPermissionToAccessException(USER_HAS_NO_PERMISSION);
        }
    }


    @Override
    @Async
    @Transactional
    public void createNotification(CreatNotificationDto creatNotificationDto) {
        Notification newNotification = buildNotificationObject(creatNotificationDto);
        saveNotification(newNotification);
    }
    
    private Notification buildNotificationObject(CreatNotificationDto notificationDto) {
        var evaluator = mapToUser(notificationDto.getEvaluator());
        var receiver = mapToUser(notificationDto.getReceiver());

        return Notification.builder()
                .status(UNREAD)
                .type(notificationDto.getNotificationType())
                .createdAt(now())
                .expireAt(now().plusDays(AppConstant.NOTIFIC_ACTIVE_DAYS_TIME))
                .relatedEntityId(notificationDto.getRelatedEntityId())
                .evaluator(evaluator)
                .receiver(receiver)
                .build();
    }
    
    private User mapToUser(UserVO userVO) {
        return modelMapper.map(userVO, User.class);
    }
    
    private void saveNotification(Notification newNotification) {
        notificationRepo.save(newNotification);
    }

}



