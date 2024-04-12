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
public class PlaceSubscribeResponseDto {
    @NotBlank
    private EmailNotification emailNotification;

    @NotBlank
    private Long userId;
}
