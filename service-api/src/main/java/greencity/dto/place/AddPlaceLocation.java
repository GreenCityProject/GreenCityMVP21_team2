package greencity.dto.place;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class AddPlaceLocation {

    private String address;

    private String addressEng;

    private Double lat;

    private Double lng;
}
