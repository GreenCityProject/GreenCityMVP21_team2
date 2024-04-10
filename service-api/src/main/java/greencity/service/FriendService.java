package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.UserManagementDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FriendService {
    ResponseEntity<Object> acceptFriendRequest(long userId, long friendId);

    ResponseEntity<Object> addNewFriend(long userId, long friendId);

    ResponseEntity<Object> declineFriendRequest(long friendId);

    ResponseEntity<Object> deleteUserFriend(long userId, long friendId);

    List<UserManagementDto> findAllFriends(Long userId);

    PageableDto findAllNotFriends(Pageable pageable);

    ResponseEntity<Object> findUserFriends(long userId);

    PageableDto getAllFriendRequests(Pageable pageable);
}
