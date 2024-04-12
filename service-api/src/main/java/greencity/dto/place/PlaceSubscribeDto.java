package greencity.dto.place;

import greencity.enums.EmailNotification;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class PlaceSubscribeDto {
    @NotBlank
    private EmailNotification emailNotification;
}
