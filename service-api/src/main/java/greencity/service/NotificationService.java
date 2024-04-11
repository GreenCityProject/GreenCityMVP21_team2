package greencity.service;

import greencity.dto.notification.NotificationDto;
import greencity.dto.notification.NotificationVO;
import greencity.dto.notification.TaggedUserNotificationDto;
import greencity.dto.user.UserVO;

import java.util.List;

public interface NotificationService {
    List<NotificationVO> getAllNotifications(Long useId);

    List<NotificationVO> getLatestUnreadNotifications(Long userId);

    void removeNotificationById(UserVO user, Long id);

    NotificationVO changeStatusToRead(Long id, UserVO user);

    NotificationVO changeStatusToUnread(Long id, UserVO user);

    void createNotification (NotificationDto notificationDto);

    void createTaggedUserNotifications(TaggedUserNotificationDto taggedUserNotification);
}
