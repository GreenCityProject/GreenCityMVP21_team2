package greencity.dto.notification;

import greencity.enums.NotificationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String content;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}
