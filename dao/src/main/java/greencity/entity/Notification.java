package greencity.entity;

import greencity.constant.AppConstant;

import greencity.enums.NotificationStatus;
import greencity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of ={"id","status","type"})
@Entity
@Table(name = "notification")
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private NotificationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private NotificationType type;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt = now();

    @Column(nullable = false, name = "expire_at")
    private LocalDateTime expireAt = now().plusDays(AppConstant.NOTIFIC_ACTIVE_DAYS_TIME);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "evaluator_id")
    private User evaluator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "receiver_id")
    private User receiver;

    @Column(nullable = false, name = "related_entity_id")
    private Long relatedEntityId;

}
