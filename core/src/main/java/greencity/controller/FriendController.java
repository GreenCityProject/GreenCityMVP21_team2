package greencity.controller;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserForListDto;
import greencity.service.UserService;
import greencity.telegram.TelegramMessageSender;
import org.springframework.http.ResponseEntity;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/friends")
@Component
public class FriendController {

    private final FriendService friendService;

    private final UserService userService;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    public FriendController(FriendService friendService, UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }

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
        String userName = userService.findById(userVO.getId()).getName();
        String friendName = userService.findById(friendId).getName();
        sendTelegramMessage("Hey, " + userName + ". New friend '" + friendName + "' added!");
        Map<String, Object> response = new HashMap<>();
        response.put("message", String.format("Friend '%s' added successfully!", friendName));
        return ResponseEntity.ok(response);
    }
    private void sendTelegramMessage(String message) {
        TelegramMessageSender sender = new TelegramMessageSender(botToken);
        sender.sendMessage(chatId, message);
    }

    @DeleteMapping("/{friendId}/declineFriend")
    public ResponseEntity<Object> declineFriendRequest(@PathVariable long friendId) {
        friendService.declineFriendRequest(friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Object> deleteUserFriend(@Parameter(hidden = true) @CurrentUser UserVO userVO, @PathVariable long friendId) {
        friendService.deleteUserFriend(userVO.getId(), friendId);
        String userName = userService.findById(userVO.getId()).getName();
        String friendName = userService.findById(friendId).getName();
        sendTelegramMessage("Ooops, " + userName + "! Friend '" + friendName + "' left!");
        Map<String, Object> response = new HashMap<>();
        response.put("message", String.format("Friend '%s' deleted successfully!", friendName));
        return ResponseEntity.ok(response);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @GetMapping("/friends")
    public ResponseEntity<List<UserFriendDto>> findAllFriends(@CurrentUser UserVO userVO, Pageable pageable) {
        List<UserFriendDto> friends = friendService.findAllFriends(userVO.getId(), pageable);
        log.info("User with id {} requested all friends. Found {} friends.", userVO.getId(), friends.size());
        List<String> friendNames = new ArrayList<>();
        for (UserFriendDto friend : friends) {
            friendNames.add(friend.getName());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Friends: " + String.join(", ", friendNames));
        response.put("friends", friends);

        return ResponseEntity.ok((List<UserFriendDto>) response);
    }


    @GetMapping("/not-friends-yet")
    public ResponseEntity<PageableDto> findAllNotFriends(Pageable pageable) {
        PageableDto notFriends = friendService.findAllNotFriends(pageable);
        return ResponseEntity.ok(notFriends);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> findUserFriends(@PathVariable long userId, Pageable pageable) {
        Object result = friendService.findAllFriends(userId, pageable);
        log.info("Requested friends for user with id {}: {}", userId, result);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friendRequests")
    public ResponseEntity<PageableDto> getAllFriendRequests(Pageable pageable) {
        PageableDto requests = friendService.getAllFriendRequests(pageable);
        return ResponseEntity.ok(requests);
    }
}
