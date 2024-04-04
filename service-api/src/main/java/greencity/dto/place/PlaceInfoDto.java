package greencity.dto.place;

import greencity.dto.comment.CommentDto;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class PlaceInfoDto {

    private Long id;

    private List<CommentDto> comments;

    private List<DiscountValueDto> discountValues;

    private LocationDto location;

    private String name;

    private List<OpenHoursDto> openingHoursList;

    private Double rate;
}
