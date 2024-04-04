package greencity.dto.place;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class LocationAddressAndGeoDto {

    private String address;

    private Double lat;

    private Double lng;
}
