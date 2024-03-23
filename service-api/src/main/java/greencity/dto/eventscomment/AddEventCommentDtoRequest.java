package greencity.dto.eventscomment;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class AddEventCommentDtoRequest {

    private Long id;

    private String text;
}
