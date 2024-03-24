package greencity.service;

import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getAllNotifications(String userEmail, String lang);

    List<NotificationDto> getLatestUnreadNotifications(String userEmail, String lang);

    void removeNotificationById(UserVO user, Long id);

    NotificationDto changeStatusToRead(Long id, UserVO user, String lang);

    NotificationDto changeStatusToUnread(Long id, UserVO user, String lang);
}
