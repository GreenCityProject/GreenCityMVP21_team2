package greencity.entity;

import greencity.enums.PlaceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;

import java.util.ArrayList;
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private PlaceLocations location;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<OpeningHours> openingHours = new ArrayList<>();

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

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<Comment> placeComments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "place_rating_user_votes",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> placeRatingUserVotes = new HashSet<>();

    public Place(String name, String titleImage, PlaceStatus status) {
        this.name = name;
        this.titleImage = titleImage;
        this.status = status;
    }

    public void addOpeningHours(List<OpeningHours> openingHours){
        openingHours.forEach(h -> h.setPlace(this));
        this.openingHours = openingHours;
    }
}
