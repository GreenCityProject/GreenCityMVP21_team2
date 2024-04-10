package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import greencity.dto.habit.HabitAssignUserDurationDto;
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

@ExtendWith(SpringExtension.class)
class HabitAssignUserDurationDtoMapperTest {

    private final HabitAssignUserDurationDtoMapper habitAssignUserDurationDtoMapper = new HabitAssignUserDurationDtoMapper();
    private ZonedDateTime currentDate = ZonedDateTime.now();
    private Long id = 24L;
    private HabitAssignStatus status = HabitAssignStatus.INPROGRESS;
    private int duration = 11;
    private int habitStreak = 86;
    private int workingDays = 12;

    @Test
    void convert_HabitAssignUserDurationDtoMapperTest_ShouldMapCorrectly() {
        HabitAssign habitAssign = new HabitAssign(id, currentDate, status, duration, workingDays, habitStreak, currentDate,
                true, null, new Habit(), new User(), null, null);

        HabitAssignUserDurationDto expected = HabitAssignUserDurationDto.builder()
                .habitAssignId(id)
                .status(status)
                .userId(habitAssign.getUser().getId())
                .habitId(habitAssign.getHabit().getId())
                .duration(duration)
                .workingDays(workingDays)
                .build();

        HabitAssignUserDurationDto actual = habitAssignUserDurationDtoMapper.convert(habitAssign);

        assertNotNull(actual);
        assertEquals(habitAssign.getId(), actual.getHabitAssignId());
        assertEquals(habitAssign.getStatus(), actual.getStatus());
        assertEquals(habitAssign.getDuration(), actual.getDuration());
        assertEquals(habitAssign.getWorkingDays(), actual.getWorkingDays());
        assertEquals(expected, actual);
    }

    @Test
    void convert_HabitAssignUserDurationDtoMapperTestWithEmptySource_ShouldReturnNullPointerException() {
        HabitAssign emptyHabitAssign = new HabitAssign();

        assertThrows(NullPointerException.class, () -> {
            habitAssignUserDurationDtoMapper.convert(emptyHabitAssign);
        });
    }

    @ParameterizedTest
    @NullSource
    void convert_HabitAssignUserDurationDtoMapperTestWithNullSource_ShouldReturnNullPointerException(HabitAssign nullHabitAssign) {
        assertThrows(NullPointerException.class, () -> {
            habitAssignUserDurationDtoMapper.convert(nullHabitAssign);
        });
    }
}