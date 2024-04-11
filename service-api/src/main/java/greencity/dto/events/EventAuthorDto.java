package greencity.dto.events;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EventAuthorDto {

    private Long id;

    private String name;

    private double organizerRating;
}
