package greencity.dto.notification;

import greencity.dto.user.UserVO;
import greencity.enums.NotificationType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotificationDto {
    private UserVO evaluator;
    private NotificationType notificationType;
    private UserVO receiver;
    private Long relatedEntityId;
}
