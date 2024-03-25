package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> getAll(@ValidLanguage Locale locale, @Parameter(hidden = true) @CurrentUser UserVO userVO){
        return notificationService.getAllNotifications(userVO.getEmail(), locale.getLanguage());
    }

    @GetMapping("/latest")
    public List<NotificationDto> getLatestNotifications(@Parameter(hidden = true) @CurrentUser UserVO userVO, @ValidLanguage Locale locale){
        return notificationService.getLatestUnreadNotifications(userVO.getEmail(), locale.getLanguage());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void removeNotification(@Parameter(hidden = true) @CurrentUser UserVO userVO, @PathVariable Long id){
        notificationService.removeNotificationById(userVO,id);
    }


    @PutMapping("/{id}/read")
    public ResponseEntity<?> changeNotificationStatusToRead(@Parameter(hidden = true) @CurrentUser UserVO user,
                                                            @PathVariable Long id, @ValidLanguage Locale locale){
        var notification = notificationService.changeStatusToRead(id,user, locale.getLanguage());
        return ResponseEntity.ok(notification);
    }


    @PutMapping("/{id}/unread")
    public ResponseEntity<?> getLatestNotifications(@Parameter(hidden = true) @CurrentUser UserVO user,
                                                    @PathVariable Long id, @ValidLanguage Locale locale){
        var notification = notificationService.changeStatusToUnread(id,user,locale.getLanguage());
        return ResponseEntity.ok(notification);
    }

}
