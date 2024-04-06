package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column
    private Integer value;
}
