package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.UserManagementDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface FriendService {
    ResponseEntity<Object> acceptFriendRequest(long userId, long friendId);

    void addNewFriend(long userId, long friendId);

    ResponseEntity<Object> declineFriendRequest(long friendId);

    ResponseEntity<Object> deleteUserFriend(long friendId);

    PageableDto findAllFriends(Long userId);

    PageableDto findAllNotFriends(Pageable pageable);

    UserManagementDto[] findUserFriends(long userId);

    PageableDto getAllFriendRequests(Pageable pageable);
}
