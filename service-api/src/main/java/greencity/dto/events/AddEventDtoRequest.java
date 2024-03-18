package greencity.dto.events;

import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class AddEventDtoRequest {

    private List<EventDateLocationDto> datesLocations;

    private String description;

    private String title;

    private List<String> tags;
}
