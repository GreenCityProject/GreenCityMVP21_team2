package greencity.dto.events;

import greencity.dto.user.UserVO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"organizer", "title"})
public class EventVO {

    private Long id;

    private String description;

    private UserVO organizer;

    private String title;

    private String titleImage;
}
