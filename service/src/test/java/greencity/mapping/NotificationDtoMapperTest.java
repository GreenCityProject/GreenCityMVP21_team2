package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import greencity.ModelUtils;
import greencity.entity.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
public class NotificationDtoMapperTest {
    private final NotificationDtoMapper notificationDtoMapper = new NotificationDtoMapper();

    @Test
    void convert_withCorrectNotification_ShouldMapCorrectly() {
        var notification = ModelUtils.getNotification();
        var expected  = ModelUtils.getNotificationDto();

        var actual = notificationDtoMapper.convert(notification);

        assertEquals(expected, actual);
    }

    @Test
    void convert_withEmptyNotification_ShouldReturnNullPointerException() {
        var notification = new Notification();

        assertThrows(NullPointerException.class,
                () -> notificationDtoMapper.convert(notification));
    }

    @ParameterizedTest
    @NullSource
    void convert_withNullNotification_ShouldReturnNullPointerException(
            Notification nullNotification) {
        assertThrows(NullPointerException.class,
                ()-> notificationDtoMapper.convert(nullNotification));
    }
}
