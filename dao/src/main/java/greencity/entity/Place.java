package greencity.entity;

import greencity.enums.PlaceStatus;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(length = 70)
    private String nameEn;

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

//    @Column
//    private Integer discountValues;

    @Column
    private PlaceStatus status;

    @ManyToMany
    @JoinTable(
            name = "places_favorite",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> favorites = new HashSet<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<PlacesImages> placesImages;
}
