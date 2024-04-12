package greencity.dto.comment;

import greencity.dto.rate.EstimateAddDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReturnDto {
    private Long id;
    private String text;
    private LocalDateTime createdDate;
    private EstimateAddDto estimate;
}
