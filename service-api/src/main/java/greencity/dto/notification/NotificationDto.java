package greencity.dto.notification;

import greencity.enums.NotificationStatus;
import greencity.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private Long evaluatorId;
    private Long relatedEntityId;
}