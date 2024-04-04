package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "places_images")
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PlacesImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String link;

    @ManyToOne
    private Place place;
}
