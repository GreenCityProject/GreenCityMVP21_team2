package greencity.service;

import static greencity.constant.ErrorMessage.*;
import static greencity.enums.NotificationStatus.READ;
import static greencity.enums.NotificationStatus.UNREAD;
import static java.util.stream.Collectors.*;

import greencity.builder.PageableAdvancedBuilder;
import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.localization.NotificationTranslation;
import greencity.enums.NotificationStatus;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.NotificationRepo;
import greencity.repository.NotificationTranslationRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationTranslationRepo translationRepo;
    private final NotificationRepo notificationRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<NotificationDto> getAllNotifications(String userEmail, String lang) {
        var translations = getNotificationTranslations(userEmail, lang, Pageable.unpaged());
        return getPageableNotifications(translations).getPage();
    }

    private Page<NotificationTranslation> getNotificationTranslations(String userEmail, String lang, Pageable pageable) {
        return translationRepo.findAll(userEmail, lang, pageable);
    }


    @Override
    public List<NotificationDto> getLatestUnreadNotifications(String userEmail, String lang) {
        var translations = getUnreadNotificationTranslations(userEmail, lang, getLatestPageable());
        return getPageableNotifications(translations).getPage();
    }

    private Page<NotificationTranslation> getUnreadNotificationTranslations(String userEmail, String lang, Pageable pageable) {
        return translationRepo.findAllWithUnreadStatus(userEmail, lang, pageable);
    }


    private PageableAdvancedDto<NotificationDto> getPageableNotifications(Page<NotificationTranslation> translations) {
        var notificationDtos = mapToNotificationDtos(translations.getContent());

        return PageableAdvancedBuilder.getPageableAdvanced(notificationDtos, translations);
    }

    private List<NotificationDto> mapToNotificationDtos(List<NotificationTranslation> translations) {
        return translations.stream()
            .map(this::map)
            .collect(toList());
    }

    private NotificationDto map(NotificationTranslation t) {
        return modelMapper.map(t, NotificationDto.class);
    }

    private PageRequest getLatestPageable() {
        return PageRequest.ofSize(AppConstant.LATEST_NOTIFICATION_SIZE)
            .withSort(Sort.by(AppConstant.LATEST_NOTIFICATION_SORT_FIELD).descending());
    }


    @Override
    @Transactional
    public void removeNotificationById(UserVO user, Long id) {
        var notification = getNotificationWithUserById(id);
        checkUserHasPermissionToAccess(user, notification);
        removeNotification(notification);
    }

    private Notification getNotificationWithUserById(Long id) {
        return notificationRepo.findByIdFetchUser(id)
                .orElseThrow(() -> new NotFoundException(NOTIFICATION_NOT_FOUND_BY_ID + id));
    }

    private void removeNotification(Notification notification) {
        notificationRepo.delete(notification);
    }

    @Override
    @Transactional
    public NotificationDto changeStatusToRead(Long id, UserVO user, String lang) {
        var translation = getTranslationByNotificationIdAndLang(id,lang);
        checkUserHasPermissionToAccess(user,translation.getNotification());

        return changeStatus(translation, READ);
    }

    @Override
    @Transactional
    public NotificationDto changeStatusToUnread(Long id, UserVO user, String lang) {
        var translation = getTranslationByNotificationIdAndLang(id,lang);
        checkUserHasPermissionToAccess(user,translation.getNotification());

        return changeStatus(translation, UNREAD);
    }


    private NotificationTranslation getTranslationByNotificationIdAndLang(Long id, String lang){
        return translationRepo.findByNotificationIdAndLanguage(id,lang)
                .orElseThrow(() -> new NotFoundException(NOTIFICATION_NOT_FOUND_BY_ID + id));
    }

    private NotificationDto changeStatus(NotificationTranslation translation, NotificationStatus status){
        var notification = translation.getNotification();
        notification.setStatus(status);
        return map(translation);
    }


    private void checkUserHasPermissionToAccess(UserVO user, Notification notification) {
        var receiverEmail = notification.getReceiver().getEmail();
        if (!(user.getRole().equals(Role.ROLE_ADMIN)) || !(user.getEmail().equals(receiverEmail))) {
            throw new UserHasNoPermissionToAccessException(USER_HAS_NO_PERMISSION);
        }
    }

}



