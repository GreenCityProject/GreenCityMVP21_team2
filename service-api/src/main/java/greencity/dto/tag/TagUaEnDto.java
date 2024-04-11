package greencity.dto.tag;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TagUaEnDto {
    private long id;
    private String nameUa;
    private String nameEn;
}
