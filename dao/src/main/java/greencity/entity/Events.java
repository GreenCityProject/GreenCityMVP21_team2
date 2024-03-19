package greencity.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private ZonedDateTime creationDate;

    @Column(nullable = false, length = 70)
    private String title;

    @Column(nullable = false)
    private String titleImage;

    @Column(nullable = false, length = 63206)
    private String description;

    @Column(nullable = false)
    private Boolean open;

    @ManyToOne
    private User organizer;

    @ManyToMany
    @JoinTable(
            name = "events_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventDateLocation> datesLocations;

    @ManyToMany
    @JoinTable(
            name = "events_attenders",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> eventAttender = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventsImages> eventsImages;
}
