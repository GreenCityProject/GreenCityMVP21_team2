package greencity.entity;

import greencity.enums.EmailNotification;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place_updates_subscribers")
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PlaceUpdatesSubscribers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.ORDINAL)
    private EmailNotification emailNotification;
}
