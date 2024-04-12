package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.dto.notification.NotificationVO;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.NotificationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static greencity.ModelUtils.*;
import static java.util.Collections.*;
import static java.util.Objects.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes,objectMapper))
                .build();
    }

    @Test
    void getAllTest_withCorrectParameters_expectSuccess() throws Exception {
        UserVO user = new UserVO();
        user.setId(1L);
        List<NotificationVO> notifications = singletonList(getNotificationDto());

        when(notificationService.getAllNotifications(any())).thenReturn(notifications);

        mockMvc.perform(MockMvcRequestBuilders.get("/notifications")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(writeAsString(notifications)));
    }

    @Test
    void getLatestNotificationsTest_withCorrectParameters_expectSuccess() throws Exception {
        UserVO user = new UserVO();
        user.setId(1L);
        List<NotificationVO> notifications = singletonList(getNotificationDto());

        when(notificationService.getLatestUnreadNotifications(any())).thenReturn(notifications);

        mockMvc.perform(MockMvcRequestBuilders.get("/notifications/latest")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(writeAsString(notifications)));
    }

    @Test
    void removeNotificationTest_withCorrectParameters_expectSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeNotificationTest_withBadId_expectNotFound() throws Exception {
        long id = 1L;
        doThrow(NotFoundException.class).when(notificationService).removeNotificationById(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/"+id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(r -> assertEquals(NotFoundException.class, requireNonNull(r.getResolvedException()).getClass()));
    }

    @Test
    void removeNotificationTest_whenUserHasNoAccess_expectForbidden() throws Exception {
        long id = 1L;
        doThrow(UserHasNoPermissionToAccessException.class).when(notificationService).removeNotificationById(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/"+id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(r -> assertEquals(UserHasNoPermissionToAccessException.class, requireNonNull(r.getResolvedException()).
                        getClass()));
    }

    @Test
    void changeNotificationStatusToRead_withCorrectParameters_expectSuccess() throws Exception {
        NotificationVO notificationVO = getNotificationDto();

        when(notificationService.changeStatusToRead(any(), any())).thenReturn(notificationVO);

        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/1/read")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(writeAsString(notificationVO)));
    }

    @Test
    void changeNotificationStatusToReadTest_withIncorrectId_expectedNotFound() throws Exception {
        when(notificationService.changeStatusToRead(any(), any())).thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/1/read")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeNotificationStatusToRead_whenUserHasNoAccess_expectForbidden() throws Exception {

        when(notificationService.changeStatusToRead(any(), any())).thenThrow(UserHasNoPermissionToAccessException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/1/read")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeNotificationStatusToUnreadTest() throws Exception {
        NotificationVO notificationVO = getNotificationDto();

        when(notificationService.changeStatusToUnread(any(), any())).thenReturn(notificationVO);

        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/1/unread")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(writeAsString(notificationVO)));
    }

    @Test
    void changeNotificationStatusToUnreadTest_withIncorrectId_expectedNotFound() throws Exception {
        when(notificationService.changeStatusToUnread(any(), any())).thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/1/unread")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeNotificationStatusToUnread_whenUserHasNoAccess_expectForbidden() throws Exception {
        when(notificationService.changeStatusToUnread(any(), any())).thenThrow(UserHasNoPermissionToAccessException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/1/unread")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    private<T> String writeAsString(T element){
        jsonMapper.registerModule(new JavaTimeModule());
        return jsonMapper.writeValueAsString(element);
    }
}
