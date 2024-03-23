package greencity.dto.events;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class AddressDto {

    private String cityEn;

    private String cityUa;

    private String countryEn;

    private String countryUa;

    private String formattedAddressEn;

    private String formattedAddressUa;

    private String houseNumber;

    private Double latitude;

    private Double longitude;

    private String regionEn;

    private String regionUa;

    private String streetEn;

    private String streetUa;
}
