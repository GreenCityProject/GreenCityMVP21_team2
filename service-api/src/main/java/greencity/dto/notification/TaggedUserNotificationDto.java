package greencity.dto.notification;

import greencity.dto.user.UserVO;
import greencity.enums.NotificationType;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaggedUserNotificationDto {
    private UserVO evaluator;
    private NotificationType notificationType;
    private List<UserVO> receivers;
    private Long relatedEntityId;
}
