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
public class BreakTimeDto {

    private LocalTime endTime;


    private LocalTime startTime;
}
