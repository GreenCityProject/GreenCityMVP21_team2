package greencity.dto.place;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class LocationDto {

    private Long id;

    private String address;

    private Double lat;

    private Double lng;
}
