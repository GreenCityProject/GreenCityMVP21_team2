package greencity.entity.localization;

import greencity.entity.Notification;
import greencity.entity.Translation;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notification_translation")
@EqualsAndHashCode(callSuper = true, exclude = "notification")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class NotificationTranslation extends Translation {

    @ManyToOne(optional = false)
    @JoinColumn(name = "notification_id",nullable = false)
    private Notification notification;
}
