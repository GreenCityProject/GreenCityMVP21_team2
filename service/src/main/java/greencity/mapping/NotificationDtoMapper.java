package greencity.mapping;

import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoMapper extends AbstractConverter<Notification, NotificationDto> {

    @Override
    protected NotificationDto convert(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .status(notification.getStatus())
                .evaluatorId(notification.getEvaluator().getId())
                .relatedEntityId(notification.getRelatedEntityId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
