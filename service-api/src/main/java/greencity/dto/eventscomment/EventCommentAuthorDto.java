package greencity.dto.eventscomment;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EventCommentAuthorDto {

    private Long id;

    private String name;

    private String userProfilePicturePath;
}
