package greencity.dto.eventscomment;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class AmountCommentLikesDto {

    private Long id;

    private Integer amountLikes;

    private Boolean liked;

    private Long userId;
}
