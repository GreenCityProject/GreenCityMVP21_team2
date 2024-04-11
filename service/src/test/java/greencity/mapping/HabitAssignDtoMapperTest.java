package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import greencity.dto.habit.HabitAssignDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
class HabitAssignDtoMapperTest {

    private final HabitAssignDtoMapper habitAssignDtoMapper = new HabitAssignDtoMapper();
    private ZonedDateTime currentDate = ZonedDateTime.now();
    private Long id = 166L;
    private HabitAssignStatus status = HabitAssignStatus.ACTIVE;
    private int duration = 18;
    private int habitStreak = 22;
    private int workingDays = 45;

    @Test
    void convert_HabitAssignDtoMapperTest_ShouldMapCorrectly() {
        HabitAssign habitAssign = new HabitAssign(id, currentDate, status, duration, workingDays, habitStreak, currentDate,
                true, null, new Habit(), new User(), null, new ArrayList<>());

        HabitAssignDto expected = HabitAssignDto.builder()
                .id(id)
                .status(status)
                .createDateTime(currentDate)
                .userId(habitAssign.getUser().getId())
                .duration(duration)
                .habitStreak(habitStreak)
                .workingDays(workingDays)
                .lastEnrollmentDate(currentDate)
                .habitStatusCalendarDtoList(new ArrayList<>())
                .build();

        HabitAssignDto actual = habitAssignDtoMapper.convert(habitAssign);

        assertNotNull(actual);
        assertEquals(habitAssign.getId(), actual.getId());
        assertEquals(habitAssign.getStatus(), actual.getStatus());
        assertEquals(habitAssign.getDuration(), actual.getDuration());
        assertEquals(habitAssign.getHabitStreak(), actual.getHabitStreak());
        assertEquals(habitAssign.getWorkingDays(), actual.getWorkingDays());
        assertEquals(habitAssign.getCreateDate(), actual.getCreateDateTime());
        assertEquals(expected, actual);
    }

    @Test
    void convert_HabitAssignDtoMapperTestWithEmptySource_ShouldReturnNullPointerException() {
        HabitAssign emptyHabitAssign = new HabitAssign();

        assertThrows(NullPointerException.class, () -> {
            habitAssignDtoMapper.convert(emptyHabitAssign);
        });
    }

    @ParameterizedTest
    @NullSource
    void convert_HabitAssignDtoMapperTestWithNullSource_ShouldReturnNullPointerException(HabitAssign nullHabitAssign) {
        assertThrows(NullPointerException.class, () -> {
            habitAssignDtoMapper.convert(nullHabitAssign);
        });
    }
}