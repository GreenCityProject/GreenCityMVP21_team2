package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.entity.*;
import greencity.enums.HabitAssignStatus;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
class HabitAssignMapperTest {

    private final HabitAssignMapper habitAssignMapper = new HabitAssignMapper();
    private ZonedDateTime currentDate = ZonedDateTime.now();
    private Long id = 15L;
    private int duration = 10;
    private int habitStreak = 32;
    private int workingDays = 5;
    private HabitAssignStatus status = HabitAssignStatus.ACTIVE;
    private HabitAssignDto habitAssignDto = new HabitAssignDto(currentDate, duration, new HabitDto(), new ArrayList<>(), null,
            habitStreak, id, currentDate, status, 0L, workingDays, true);
    private UserShoppingListItemAdvanceDto userShoppingListItemAdvanceDto = new UserShoppingListItemAdvanceDto(id, id, null,
            currentDate.toLocalDateTime(), "content");

    @Test
    void convert_HabitAssignMapperTest_ShouldMapCorrectly() {
        userShoppingListItemAdvanceDto.setStatus(ShoppingListItemStatus.ACTIVE);
        List<UserShoppingListItemAdvanceDto> userShoppingListItems = List.of(userShoppingListItemAdvanceDto);
        habitAssignDto.setUserShoppingListItems(userShoppingListItems);

        HabitAssign expected = expectedConvert(habitAssignDto);

        HabitAssign actual = habitAssignMapper.convert(habitAssignDto);

        assertNotNull(actual);
        assertEquals(habitAssignDto.getId(), actual.getId());
        assertEquals(habitAssignDto.getStatus(), actual.getStatus());
        assertEquals(habitAssignDto.getDuration(), actual.getDuration());
        assertEquals(habitAssignDto.getHabitStreak(), actual.getHabitStreak());
        assertEquals(habitAssignDto.getWorkingDays(), actual.getWorkingDays());
        assertNotEquals(habitAssignDto.getUserShoppingListItems(), actual.getUserShoppingListItems());
        assertEquals(expected, actual);
    }

    @Test
    void convert_HabitAssignMapperTestWithInprogresStatusSourceList_ShouldMapCorrectly() {
        userShoppingListItemAdvanceDto.setStatus(ShoppingListItemStatus.INPROGRESS);
        List<UserShoppingListItemAdvanceDto> userShoppingListItems = List.of(userShoppingListItemAdvanceDto);
        habitAssignDto.setUserShoppingListItems(userShoppingListItems);

        HabitAssign expected = expectedConvert(habitAssignDto);
        HabitAssign actual = habitAssignMapper.convert(habitAssignDto);

        UserShoppingListItem expectedList = UserShoppingListItem.builder()
                .id(userShoppingListItemAdvanceDto.getId())
                .dateCompleted(userShoppingListItemAdvanceDto.getDateCompleted())
                .status(userShoppingListItemAdvanceDto.getStatus())
                .shoppingListItem(ShoppingListItem.builder()
                        .id(userShoppingListItemAdvanceDto.getShoppingListItemId())
                        .build())
                .build();

        assertNotNull(actual);
        assertNotNull(userShoppingListItems);
        assertEquals(List.of(expectedList), actual.getUserShoppingListItems());
        assertEquals(expected, actual);
    }

    @Test
    void convert_HabitAssignMapperTestWithEmptySource_ShouldReturnNullPointerException() {
        HabitAssignDto emptyHabitAssignDto = new HabitAssignDto();

        assertThrows(NullPointerException.class, () -> {
            habitAssignMapper.convert(emptyHabitAssignDto);
        });
    }

    @ParameterizedTest
    @NullSource
    void convert_HabitAssignMapperTestWithNullSource_ShouldReturnNullPointerException(HabitAssignDto nullHabitAssignDto) {
        assertThrows(NullPointerException.class, () -> {
            habitAssignMapper.convert(nullHabitAssignDto);
        });
    }

    private HabitAssign expectedConvert(HabitAssignDto dto) {
            List<UserShoppingListItem> listOfShoppingListItem = dto.getUserShoppingListItems().stream()
                    .filter(item -> item.getStatus() == ShoppingListItemStatus.INPROGRESS)
                    .map(item -> UserShoppingListItem.builder()
                            .id(item.getId())
                            .dateCompleted(item.getDateCompleted())
                            .status(item.getStatus())
                            .shoppingListItem(ShoppingListItem.builder()
                                    .id(item.getShoppingListItemId())
                                    .build())
                            .build())
                    .collect(Collectors.toList());

            return HabitAssign.builder()
                    .id(id)
                    .duration(duration)
                    .habitStreak(habitStreak)
                    .createDate(currentDate)
                    .status(status)
                    .workingDays(workingDays)
                    .lastEnrollmentDate(currentDate)
                    .habit(Habit.builder()
                            .id(dto.getHabit().getId())
                            .complexity(dto.getHabit().getComplexity())
                            .defaultDuration(dto.getDuration())
                            .build())
                    .userShoppingListItems(listOfShoppingListItem)
                    .build();
    }

}