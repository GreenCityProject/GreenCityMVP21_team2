package greencity.dto.place;

import greencity.enums.WeekDay;
import lombok.*;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class OpeningHoursDto {
    private BreakTimeDto breakTime;


    private LocalTime closeTime;


    private LocalTime openTime;

    private WeekDay weekDay;
}
