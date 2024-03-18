package greencity.dto.events;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EventCommentAuthorDto {
    @NotEmpty
    @NotNull
    @Min(1)
    private Long id;

    private String name;

    private String userProfilePicturePath;
}
