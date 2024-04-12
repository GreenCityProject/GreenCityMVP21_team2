package greencity.dto.place;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class BulkUpdatePlaceStatusDto {

    private List<Long> ids;

    private String status;
}
