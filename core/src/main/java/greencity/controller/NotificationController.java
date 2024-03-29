package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /**
     * Method retrieves all notifications of currentUser
     *
     * @return new {@link List<NotificationDto>}
     * @author DenysRyhal
     */
    @Operation(summary = "Get all notifications of current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping
    public List<NotificationDto> getAll(@CurrentUser UserVO userVO){
        return notificationService.getAllNotifications(userVO.getId());
    }

    /**
     * Method retrieves the latest unread notifications of currentUser
     *
     * @return new {@link List<NotificationDto>}
     * @author DenysRyhal
     */
    @Operation(summary = "Get latest unread notifications of current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/latest")
    public List<NotificationDto> getLatestNotifications(@Parameter(hidden = true) @CurrentUser UserVO userVO){
        return notificationService.getLatestUnreadNotifications(userVO.getId());
    }

    /**
     * Method removes notification by id
     *
     * @param id {@link Long} notification id
     * @author DenysRyhal
     */
    @Operation(summary = "Remove notification by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = HttpStatuses.NO_CONTENT),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void removeNotification(@Parameter(hidden = true) @CurrentUser UserVO userVO, @PathVariable Long id){
        notificationService.removeNotificationById(userVO,id);
    }


    /**
     * Method changes notification status to READ notification
     *
     * @param id {@link Long} notification id
     * @author DenysRyhal
     * @return {@link ResponseEntity<NotificationDto>}
     */
    @Operation(summary = "mark notification UNREAD by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDto> changeNotificationStatusToRead(@Parameter(hidden = true) @CurrentUser UserVO user,
                                                            @PathVariable Long id){
        var notification = notificationService.changeStatusToRead(id,user);
        return ResponseEntity.ok(notification);
    }

    /**
     * Method changes notification status to UNREAD notification
     *
     * @param id {@link Long} notification id
     * @author DenysRyhal
     * @return {@link ResponseEntity<NotificationDto>}
     */
    @Operation(summary = "mark notification UNREAD by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
    })
    @PatchMapping("/{id}/unread")
    public ResponseEntity<NotificationDto> getLatestNotifications(@Parameter(hidden = true) @CurrentUser UserVO user,
                                                    @PathVariable Long id){
        var notification = notificationService.changeStatusToUnread(id,user);
        return ResponseEntity.ok(notification);
    }

}
