package greencity.entity;

import greencity.enums.PlaceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "places")
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 70)
    private String name;

    @Column(nullable = false)
    private String titleImage;

    @ManyToOne
    private Category category;

    @OneToOne
    private PlaceLocations location;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<OpeningHours> openingHours;

    @ManyToOne
    private User author;

    @ManyToMany
    @JoinTable(
            name = "place_discount",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "specifications_id"))
    private Set<Specification> discountValues;

    @Column
    @JdbcType(IntegerJdbcType.class)
    private PlaceStatus status;

    @Column
    private Double rating;

    @ManyToMany
    @JoinTable(
            name = "places_favorite",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> favorites = new HashSet<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<PlacesImages> placesImages;

    @ManyToMany
    @JoinTable(
            name = "place_rating_user_votes",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> placeRatingUserVotes = new HashSet<>();
}
