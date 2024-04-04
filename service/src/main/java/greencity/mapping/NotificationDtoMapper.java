package greencity.mapping;

import greencity.dto.notification.NotificationVO;
import greencity.entity.Notification;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoMapper extends AbstractConverter<Notification, NotificationVO> {

    @Override
    protected NotificationVO convert(Notification notification) {
        return NotificationVO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .status(notification.getStatus())
                .evaluatorId(notification.getEvaluator().getId())
                .relatedEntityId(notification.getRelatedEntityId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
