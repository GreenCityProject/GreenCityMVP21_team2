package greencity.dto.place;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MapBoundsDto {
    double northEastLat;
    double northEastLng;
    double southWestLat;
    double southWestLng;
}
