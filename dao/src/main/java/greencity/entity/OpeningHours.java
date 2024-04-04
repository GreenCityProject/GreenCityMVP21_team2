package greencity.entity;

import greencity.enums.WeekDay;
import jakarta.persistence.*;
import lombok.*;

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
    private Place place;

    @Column
    private LocalTime openTime;

    @Column
    private LocalTime closeTime;

    @Column
    private LocalTime breakTimeStart;

    @Column
    private LocalTime breakTimeEnd;

    @Column
    private WeekDay weekDay;
}
