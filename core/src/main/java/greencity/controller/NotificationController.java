package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> getAll(@CurrentUser UserVO userVO, @ValidLanguage Locale locale){
        return notificationService.getAllNotifications(userVO.getEmail(), locale.getLanguage());
    }

    @GetMapping("/latest")
    public List<NotificationDto> getLatestNotifications(@CurrentUser UserVO userVO, @ValidLanguage Locale locale){
        return notificationService.getLatestUnreadNotifications(userVO.getEmail(), locale.getLanguage());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void getLatestNotifications(@CurrentUser UserVO userVO, @PathVariable Long id){
        notificationService.removeNotificationById(userVO,id);
    }



}
