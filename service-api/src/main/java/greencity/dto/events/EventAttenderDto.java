package greencity.dto.events;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EventAttenderDto {

    private Long id;

    private String name;

    private String imagePath;
}
