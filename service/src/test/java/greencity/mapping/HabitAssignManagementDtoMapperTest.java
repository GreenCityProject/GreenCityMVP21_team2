package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import greencity.dto.habit.HabitAssignManagementDto;
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
class HabitAssignManagementDtoMapperTest {

    private final HabitAssignManagementDtoMapper habitAssignManagementDtoMapper = new HabitAssignManagementDtoMapper();
    private ZonedDateTime currentDate = ZonedDateTime.now().minusMonths(2);
    private Long id = 885L;
    private HabitAssignStatus status = HabitAssignStatus.INPROGRESS;
    private int duration = 48;
    private int habitStreak = 115;
    private int workingDays = 63;

    @Test
    void convert_HabitAssignManagementDtoMapperTest_ShouldMapCorrectly() {
        HabitAssign habitAssign = new HabitAssign(id, currentDate, status, duration, workingDays, habitStreak, currentDate,
                true, null, new Habit(), new User(), null, null);

        HabitAssignManagementDto expected = HabitAssignManagementDto.builder()
                .id(id)
                .status(status)
                .createDateTime(currentDate)
                .userId(habitAssign.getUser().getId())
                .habitId(habitAssign.getHabit().getId())
                .duration(duration)
                .habitStreak(habitStreak)
                .workingDays(workingDays)
                .lastEnrollment(currentDate)
                .build();

        HabitAssignManagementDto actual = habitAssignManagementDtoMapper.convert(habitAssign);

        assertNotNull(actual);
        assertEquals(habitAssign.getId(), actual.getId());
        assertEquals(habitAssign.getStatus(), actual.getStatus());
        assertEquals(habitAssign.getDuration(), actual.getDuration());
        assertEquals(habitAssign.getHabitStreak(), actual.getHabitStreak());
        assertEquals(habitAssign.getWorkingDays(), actual.getWorkingDays());
        assertEquals(habitAssign.getCreateDate(), actual.getCreateDateTime());
        assertEquals(habitAssign.getLastEnrollmentDate(), actual.getLastEnrollment());
        assertEquals(expected, actual);
    }

    @Test
    void convert_HabitAssignManagementDtoMapperTestWithEmptySource_ShouldReturnNullPointerException() {
        HabitAssign emptyHabitAssign = new HabitAssign();

        assertThrows(NullPointerException.class, () -> {
            habitAssignManagementDtoMapper.convert(emptyHabitAssign);
        });
    }

    @ParameterizedTest
    @NullSource
    void convert_HabitAssignManagementDtoMapperTestWithNullSource_ShouldReturnNullPointerException(HabitAssign nullHabitAssign) {
        assertThrows(NullPointerException.class, () -> {
            habitAssignManagementDtoMapper.convert(nullHabitAssign);
        });
    }
}