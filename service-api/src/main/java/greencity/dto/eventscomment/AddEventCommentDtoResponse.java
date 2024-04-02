package greencity.dto.eventscomment;

import lombok.*;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class AddEventCommentDtoResponse {

    private Long id;

    private EventCommentAuthorDto author;

    private ZonedDateTime createdDate;

    private String text;
}
