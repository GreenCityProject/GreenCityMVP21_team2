package greencity.dto.place;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class PlaceAuthorDto {

    private Long id;

    private String name;

    private String email;
}
