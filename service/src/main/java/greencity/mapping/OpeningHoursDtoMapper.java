package greencity.mapping;

import greencity.dto.place.OpeningHoursDto;
import greencity.entity.BreakTime;
import greencity.entity.OpeningHours;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import static java.util.Objects.*;

@Component
public class OpeningHoursDtoMapper extends AbstractConverter<OpeningHoursDto, OpeningHours> {

    @Override
    protected OpeningHours convert(OpeningHoursDto dto) {
        return OpeningHours.builder()
                .weekDay(dto.getWeekDay())
                .openTime(dto.getOpenTime())
                .closeTime(dto.getCloseTime())
                .breakTime(nonNull(dto.getBreakTime()) ? getBreakTime(dto) : null)
                .build();
    }

    private BreakTime getBreakTime(OpeningHoursDto dto) {
        return BreakTime.builder()
                .startTime(dto.getBreakTime().getStartTime())
                .endTime(dto.getBreakTime().getEndTime())
                .build();
    }
}
