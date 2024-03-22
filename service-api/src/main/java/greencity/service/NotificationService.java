package greencity.service;

import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getAllNotifications(String userEmail, String lang);

    List<NotificationDto> getLatestNotifications(String userEmail, String lang);

    void removeNotificationById(UserVO userVO, Long id);
}
