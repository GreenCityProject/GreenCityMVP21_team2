package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.service.FriendService;
import greencity.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;

    @Autowired
    public FriendController(FriendService friendService, UserService userService) {
        this.friendService = friendService;
    }

    @PatchMapping("/{friendId}/acceptFriend")
    public ResponseEntity<Object> acceptFriendRequest(@PathVariable long friendId) {
        friendService.acceptFriendRequest(friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{friendId}")
    public ResponseEntity<Object> addNewFriend(@PathVariable long friendId) {
        friendService.addNewFriend(friendId);
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
    @ApiOperation("Get all friends")
    @GetMapping
    public ResponseEntity<PageableDto> findAllFriends(Pageable pageable) {
        PageableDto friends = friendService.findAllFriends(pageable);
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
