package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "events_images")
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventsImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String link;

    @ManyToOne
    private Events event;
}
