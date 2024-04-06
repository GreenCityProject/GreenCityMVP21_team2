package greencity.dto.rate;

import greencity.constant.ServiceValidationConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * The data transfer object of the Estimate.
 *
 * @author Marian Milian
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimateAddDto {
    @Min(value = 1, message = ServiceValidationConstants.RATE_MIN_VALUE)
    @Max(value = 5, message = ServiceValidationConstants.RATE_MAX_VALUE)
    private Byte rate;
}
