package greencity.entity;

import greencity.constant.AppConstant;
import greencity.entity.localization.NotificationTranslation;
import greencity.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notification",indexes =
        @Index(name = "IX_notification_receiver_id",columnList = "receiver_id"))
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private NotificationStatus status;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt = now();

    @Column(nullable = false, name = "expire_at")
    private LocalDateTime expireAt = now().plusDays(AppConstant.NOTIFIC_ACTIVE_DAYS_TIME);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "receiver_id")
    private User receiver;

    @OneToMany(mappedBy = "notification",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<NotificationTranslation> translations = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification that)) return false;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
