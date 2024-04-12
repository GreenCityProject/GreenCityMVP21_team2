package greencity.dto.newsSubscriber;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static greencity.constant.AppConstant.VALIDATION_EMAIL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsSubscriberRequestDto {
    @NotBlank
    @Email
    @Pattern(regexp = VALIDATION_EMAIL)
    private String email;
}
