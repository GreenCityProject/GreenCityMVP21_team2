package greencity.dto.place;

import lombok.*;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class OpenHoursDto {

    private Long id;

    private BreakTimeDto breakTime;

    private LocalTime closeTime;

    private LocalTime openTime;

    private String weekDay;
}
