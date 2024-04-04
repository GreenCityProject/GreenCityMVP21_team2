package greencity.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "discount_values")
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DiscountValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "discounts_specifications",
            joinColumns = @JoinColumn(name = "discount_value_id"),
            inverseJoinColumns = @JoinColumn(name = "specifications_id"))
    private Set<Specification> specification;

    @Column
    private Integer value;
}
