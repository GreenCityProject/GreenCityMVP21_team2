package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.user.UserVO;
import greencity.enums.HabitRate;
import greencity.service.HabitStatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class HabitStatisticControllerTest {
    private MockMvc mockMvc;
    private static final String habitStatisticControllerLink = "/habit/statistic";
    @Mock
    private HabitStatisticService habitStatisticService;
    @InjectMocks
    private HabitStatisticController habitStatisticController;
    private UserVO userVO = new UserVO();
    private ObjectMapper objectMapper = new ObjectMapper();
    private final Long id = 1l;

    @BeforeEach
    void setUp(){
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController)
                .build();
    }

    @Test
    void findAllByHabitId_HabitStatisticControllerTest_shouldReturnIsOkStatus() throws Exception{
        mockMvc.perform(get(habitStatisticControllerLink + "/{habitId}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(habitStatisticService).findAllStatsByHabitId(id);
    }

    @Test
    void findAllStatsByHabitAssignId_HabitStatisticControllerTest_shouldReturnIsOkStatus() throws Exception{
        mockMvc.perform(get(habitStatisticControllerLink + "/assign/{habitAssignId}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(habitStatisticService).findAllStatsByHabitAssignId(id);
    }

    @Test
    void saveHabitStatistic_HabitStatisticControllerTest_shouldReturnIsCreatedStatus() throws Exception{
        String addHabitStatisticDtoToString = "{\"amountOfItems\":10,\"habitRate\":\"GOOD\",\"createDate\":\"" + ZonedDateTime.now().toString() + "\"}";
        when(habitStatisticService.saveByHabitIdAndUserId(eq(id), eq(userVO.getId()), any()))
                .thenReturn(new HabitStatisticDto());
        mockMvc.perform(post(habitStatisticControllerLink + "/{habitId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addHabitStatisticDtoToString))
                .andExpect(status().isCreated());
        verify(habitStatisticService).saveByHabitIdAndUserId(eq(id), eq(userVO.getId()), any());
    }
    @Test
    void updateStatistic_HabitStatisticControllerTest_shouldReturnIsOkStatus() throws Exception{
        UpdateHabitStatisticDto updateDto = UpdateHabitStatisticDto.builder()
                .amountOfItems(1)
                .habitRate(HabitRate.GOOD)
                .build();
        when(habitStatisticService.update(anyLong(), anyLong(), eq(updateDto)))
                .thenReturn(new UpdateHabitStatisticDto());
        mockMvc.perform(put(habitStatisticControllerLink + "/{habitId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
        verify(habitStatisticService).update(anyLong(), anyLong(), eq(updateDto));
    }

   @Test
    void getTodayStatisticsForAllHabitItems_HabitStatisticControllerTest_shouldReturnIsOkStatus() throws Exception{
        Locale locale = new Locale("en");
        when(habitStatisticService.getTodayStatisticsForAllHabitItems(locale.getLanguage()))
                .thenReturn(List.of(new HabitItemsAmountStatisticDto()));
        mockMvc.perform(get(habitStatisticControllerLink + "/todayStatisticsForAllHabitItems", id)
                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
        verify(habitStatisticService).getTodayStatisticsForAllHabitItems(locale.getLanguage());
   }

   @Test
   void findAmountOfAcquiredHabits_HabitStatisticControllerTest_shouldReturnIsOkStatus() throws Exception{
        mockMvc.perform(get(habitStatisticControllerLink + "/acquired/count")
                        .param("userId", Long.toString(id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(habitStatisticService).getAmountOfAcquiredHabitsByUserId(id);
   }

   @Test
    void findAmountOfHabitsInProgress_HabitStatisticControllerTest_shouldReturnIsOkStatus() throws Exception{
       mockMvc.perform(get(habitStatisticControllerLink + "/in-progress/count")
                       .param("userId", Long.toString(id))
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
       verify(habitStatisticService).getAmountOfHabitsInProgressByUserId(id);
   }
}
