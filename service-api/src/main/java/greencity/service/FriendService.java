package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface FriendService {
    ResponseEntity<Object> acceptFriendRequest(long friendId);

    ResponseEntity<Object> addNewFriend(long friendId);

    ResponseEntity<Object> declineFriendRequest(long friendId);

    ResponseEntity<Object> deleteUserFriend(long friendId);

    PageableDto findAllFriends(Pageable pageable);

    PageableDto findAllNotFriends(Pageable pageable);

    UserManagementDto[] findUserFriends(long userId);

    PageableDto getAllFriendRequests(Pageable pageable);
}
