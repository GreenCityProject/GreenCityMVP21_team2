package greencity.mapping;

import greencity.dto.notification.NotificationDto;
import greencity.entity.localization.NotificationTranslation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoMapper extends AbstractConverter<NotificationTranslation, NotificationDto> {

    @Override
    protected NotificationDto convert(NotificationTranslation translation) {
        var notification = translation.getNotification();
        return NotificationDto.builder()
            .id(notification.getId())
            .content(translation.getContent())
            .status(notification.getStatus())
            .createdAt(notification.getCreatedAt())
            .build();
    }
}
