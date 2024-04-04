package greencity.mapping;

import greencity.dto.place.OpeningHoursDto;
import greencity.entity.BreakTime;
import greencity.entity.OpeningHours;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class OpeningHoursDtoMapper extends AbstractConverter<OpeningHoursDto, OpeningHours> {

    @Override
    protected OpeningHours convert(OpeningHoursDto dto) {
        return OpeningHours.builder()
                .weekDay(dto.getWeekDay())
                .openTime(dto.getCloseTime())
                .closeTime(dto.getCloseTime())
                .breakTime(BreakTime.builder()
                        .startTime(dto.getBreakTime().getStartTime())
                        .endTime(dto.getBreakTime().getEndTime())
                        .build()
                ).build();
    }
}
