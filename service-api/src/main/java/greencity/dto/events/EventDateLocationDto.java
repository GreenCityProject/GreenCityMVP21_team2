package greencity.dto.events;

import lombok.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EventDateLocationDto {

    private Long id;

    private AddressDto coordinates;

//    private EventDto event;

    private ZonedDateTime startDate;

    private ZonedDateTime finishDate;

    private String onlineLink;
}
