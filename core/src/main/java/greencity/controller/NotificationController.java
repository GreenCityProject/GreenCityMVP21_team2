package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> getAll(@CurrentUser UserVO userVO){
        return notificationService.getAllNotifications(userVO.getId());
    }

    @GetMapping("/latest")
    public List<NotificationDto> getLatestNotifications(@Parameter(hidden = true) @CurrentUser UserVO userVO){
        return notificationService.getLatestUnreadNotifications(userVO.getId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void removeNotification(@Parameter(hidden = true) @CurrentUser UserVO userVO, @PathVariable Long id){
        notificationService.removeNotificationById(userVO,id);
    }


    @PutMapping("/{id}/read")
    public ResponseEntity<?> changeNotificationStatusToRead(@Parameter(hidden = true) @CurrentUser UserVO user,
                                                            @PathVariable Long id){
        var notification = notificationService.changeStatusToRead(id,user);
        return ResponseEntity.ok(notification);
    }


    @PutMapping("/{id}/unread")
    public ResponseEntity<?> getLatestNotifications(@Parameter(hidden = true) @CurrentUser UserVO user,
                                                    @PathVariable Long id){
        var notification = notificationService.changeStatusToUnread(id,user);
        return ResponseEntity.ok(notification);
    }

}
