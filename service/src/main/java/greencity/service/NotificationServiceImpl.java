package greencity.service;

import greencity.builder.PageableAdvancedBuilder;
import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.localization.NotificationTranslation;
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

import static greencity.constant.ErrorMessage.*;
import static java.util.stream.Collectors.*;

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
        return translationRepo.findAllNotification(userEmail, lang, pageable);
    }


    @Override
    public List<NotificationDto> getLatestUnreadNotifications(String userEmail, String lang) {
        var translations = getUnreadNotificationTranslations(userEmail, lang, getLatestPageable());
        return getPageableNotifications(translations).getPage();
    }

    private Page<NotificationTranslation> getUnreadNotificationTranslations(String userEmail, String lang, Pageable pageable) {
        return translationRepo.findAllUnreadNotification(userEmail, lang, pageable);
    }


    private PageableAdvancedDto<NotificationDto> getPageableNotifications(Page<NotificationTranslation> translations) {
        var notificationDtos = mapToNotificationDtos(translations.getContent());

        return PageableAdvancedBuilder.getPageableAdvanced(notificationDtos, translations);
    }

    private List<NotificationDto> mapToNotificationDtos(List<NotificationTranslation> translations) {
        return translations.stream()
            .map(t -> modelMapper.map(t, NotificationDto.class))
            .collect(toList());
    }

    private PageRequest getLatestPageable() {
        return PageRequest.ofSize(AppConstant.LATEST_NOTIFICATION_SIZE)
            .withSort(Sort.by(AppConstant.LATEST_NOTIFICATION_SORT_FIELD).descending());
    }


    @Override
    @Transactional
    public void removeNotificationById(UserVO userVO, Long id) {
        var notification = getNotificationWithUser(id);

        checkUserHasPermissionToAccess(userVO, notification);
        removeNotification(notification);
    }

    private Notification getNotificationWithUser(Long id) {
        return notificationRepo.findByIdFetchUser(id)
                .orElseThrow(() -> new NotFoundException(NOTIFICATION_NOT_FOUND_BY_ID + id));
    }

    private void checkUserHasPermissionToAccess(UserVO user, Notification notification) {
        var receiverEmail = notification.getReceiver().getEmail();

        if (!(user.getEmail().equals(receiverEmail)) || !(user.getRole().equals(Role.ROLE_ADMIN))) {
            throw new UserHasNoPermissionToAccessException(USER_HAS_NO_PERMISSION);
        }
    }

    private void removeNotification(Notification notification) {
        notificationRepo.delete(notification);
    }
}
