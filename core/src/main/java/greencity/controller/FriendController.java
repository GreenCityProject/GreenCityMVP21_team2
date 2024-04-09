package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "Accept friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @PatchMapping("/{friendId}/acceptFriend")
    public ResponseEntity<Object> acceptFriendRequest(@PathVariable long friendId,
                                                      @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        friendService.acceptFriendRequest(userVO.getId(), friendId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add new friend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @PostMapping("/{friendId}")
    public ResponseEntity<Object> addNewFriend(@PathVariable long friendId, @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        friendService.addNewFriend(userVO.getId(), friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}/declineFriend")
    public ResponseEntity<Object> declineFriendRequest(@PathVariable long friendId) {
        friendService.declineFriendRequest(friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Object> deleteUserFriend(@PathVariable long friendId) {
        friendService.deleteUserFriend(friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PageableDto> findAllFriends(@Parameter(hidden = true) @CurrentUser UserVO userVO) {
        PageableDto friends = friendService.findAllFriends(userVO.getId());
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/not-friends-yet")
    public ResponseEntity<PageableDto> findAllNotFriends(Pageable pageable) {
        PageableDto notFriends = friendService.findAllNotFriends(pageable);
        return ResponseEntity.ok(notFriends);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserManagementDto[]> findUserFriends(@PathVariable long userId) {
        UserManagementDto[] friends = friendService.findUserFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/friendRequests")
    public ResponseEntity<PageableDto> getAllFriendRequests(Pageable pageable) {
        PageableDto requests = friendService.getAllFriendRequests(pageable);
        return ResponseEntity.ok(requests);
    }
}
