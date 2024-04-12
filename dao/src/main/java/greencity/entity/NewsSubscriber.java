package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "news_subscribers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class NewsSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String unsubscribeToken;

}
