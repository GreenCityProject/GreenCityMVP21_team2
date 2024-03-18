package greencity.dto.events;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "event")
@Builder
@EqualsAndHashCode
public class EventDateLocationDto {

    private Long id;

    private AddressDto coordinates;

    private EventDto event;

    private LocalDateTime startDate;

    private LocalDateTime finishDate;

    private String onlineLink;
}
