package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendControllerTest {
    @Mock
    private FriendService friendService;

    @InjectMocks
    private FriendController friendController;

//    @BeforeEach
//    public void setUp() {
//        friendController = new FriendController(friendService);
//    }

    @Test
    @DisplayName("Test accepting friend request")
    public void testAcceptFriendRequest() {
        long friendId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(friendService.acceptFriendRequest(friendId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = friendController.acceptFriendRequest(friendId);

        assertEquals(expectedResponse, actualResponse);
        verify(friendService, times(1)).acceptFriendRequest(friendId);
    }

    @Test
    @DisplayName("Test adding a new friend")
    public void testAddNewFriend() {
        long friendId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(friendService.addNewFriend(friendId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = friendController.addNewFriend(friendId);

        assertEquals(expectedResponse, actualResponse);
        verify(friendService, times(1)).addNewFriend(friendId);
    }

    @Test
    @DisplayName("Test declining friend request")
    public void testDeclineFriendRequest() {
        long friendId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(friendService.declineFriendRequest(friendId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = friendController.declineFriendRequest(friendId);

        assertEquals(expectedResponse, actualResponse);
        verify(friendService, times(1)).declineFriendRequest(friendId);
    }

    @Test
    @DisplayName("Test deleting a user's friend")
    public void testDeleteUserFriend() {
        long friendId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(friendService.deleteUserFriend(friendId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = friendController.deleteUserFriend(friendId);

        assertEquals(expectedResponse, actualResponse);
        verify(friendService, times(1)).deleteUserFriend(friendId);
    }

    @Test
    @DisplayName("Test finding all friends")
    public void testFindAllFriends() {
        Pageable pageable = Pageable.unpaged();
        PageableDto expectedResponse = new PageableDto(Collections.emptyList(), 0, 0, 0);

        when(friendService.findAllFriends(pageable)).thenReturn(expectedResponse);

        ResponseEntity<PageableDto> actualResponse = friendController.findAllFriends(pageable);

        assertEquals(expectedResponse, actualResponse.getBody());
        assertEquals(200, actualResponse.getStatusCodeValue());
    }

    @Test
    @DisplayName("Test finding all friends who are not yet friends")
    public void testFindAllNotFriends() {
        Pageable pageable = Pageable.unpaged();
        PageableDto expectedResponse = new PageableDto(Collections.emptyList(), 0, 0, 0);

        when(friendService.findAllNotFriends(pageable)).thenReturn(expectedResponse);

        ResponseEntity<PageableDto> actualResponse = friendController.findAllNotFriends(pageable);

        assertEquals(expectedResponse, actualResponse.getBody());
        assertEquals(200, actualResponse.getStatusCodeValue());
    }

    @Test
    @DisplayName("Test finding a user's friends")
    public void testFindUserFriends() {
        long userId = 1L;
        UserManagementDto[] expectedResponse = {new UserManagementDto(1L, "John", "john@example.com")};

        when(friendService.findUserFriends(userId)).thenReturn(expectedResponse);

        ResponseEntity<UserManagementDto[]> actualResponse = friendController.findUserFriends(userId);

        assertEquals(expectedResponse, actualResponse.getBody());
        assertEquals(200, actualResponse.getStatusCodeValue());
    }

    @Test
    @DisplayName("Test getting all friend requests")
    public void testGetAllFriendRequests() {
        Pageable pageable = Pageable.unpaged();
        PageableDto expectedResponse = new PageableDto(Collections.emptyList(), 0, 0, 0);

        when(friendService.getAllFriendRequests(pageable)).thenReturn(expectedResponse);

        ResponseEntity<PageableDto> actualResponse = friendController.getAllFriendRequests(pageable);

        assertEquals(expectedResponse, actualResponse.getBody());
        assertEquals(200, actualResponse.getStatusCodeValue());
    }
}
