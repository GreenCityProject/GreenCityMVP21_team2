package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "event_comment")
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8000)
    private String text;


    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDate;

    @Column
    private ZonedDateTime editingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    private Events event;

    @ManyToOne
    private EventComment eventParentComment;

    @ManyToMany
    @JoinTable(
            name = "event_comment_users_liked",
            joinColumns = @JoinColumn(name = "event_comment_id"),
            inverseJoinColumns = @JoinColumn(name = "users_liked_id"))
    private Set<User> usersLiked;
}
