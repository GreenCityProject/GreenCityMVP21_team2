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
public class EventCommentDto {

    private Long id;

    private EventCommentAuthorDto author;

    private ZonedDateTime createdDate;

    private Boolean currentUserLiked;

    private Integer numberOfLikes;

    private Integer numberOfReplies;

    private String text;
}
