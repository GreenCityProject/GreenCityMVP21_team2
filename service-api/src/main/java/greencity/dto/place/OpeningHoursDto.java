package greencity.dto.place;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class OpeningHoursDto {

    private BreakTimeDto breakTime;

    private String closeTime;

    private String openTime;

    private String weekDay;
}
