package greencity.entity.localization;

import greencity.entity.Language;
import greencity.entity.Notification;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notification_translation")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = {"id","content"})
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Language language;

    @Column(nullable = false)
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "notification_id",nullable = false)
    private Notification notification;
}
