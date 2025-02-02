package greencity.entity;

import greencity.enums.WeekDay;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;

import java.time.LocalTime;

@Entity
@Table(name = "opening_hours")
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OpeningHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Column
    private LocalTime openTime;

    @Column
    private LocalTime closeTime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private BreakTime breakTime;

    @Column
    @JdbcType(IntegerJdbcType.class)
    private WeekDay weekDay;
}
