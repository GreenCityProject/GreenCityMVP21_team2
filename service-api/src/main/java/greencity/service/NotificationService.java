package greencity.service;

import greencity.dto.CreatNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getAllNotifications(Long useId);

    List<NotificationDto> getLatestUnreadNotifications(Long userId);

    void removeNotificationById(UserVO user, Long id);

    NotificationDto changeStatusToRead(Long id, UserVO user);

    NotificationDto changeStatusToUnread(Long id, UserVO user);

    void createNotification (CreatNotificationDto creatNotificationDto);
}
