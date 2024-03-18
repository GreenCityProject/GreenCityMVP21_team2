package greencity.dto.events;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EventCommentDto {

    @NotEmpty
    @NotNull
    @Min(1)
    private Long id;

    private EventCommentAuthorDto author;

    private LocalDateTime createdDate;

    private Boolean currentUserLiked;

    private Integer numberOfLikes;

    private Integer numberOfReplies;

    private String text;
}
