package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import org.springframework.data.domain.Pageable;

public interface FriendService {
    void acceptFriendRequest(long friendId);

    void addNewFriend(long friendId);

    void declineFriendRequest(long friendId);

    void deleteUserFriend(long friendId);

    PageableDto findAllFriends(Pageable pageable);

    PageableDto findAllNotFriends(Pageable pageable);

    UserManagementDto[] findUserFriends(long userId);

    PageableDto getAllFriendRequests(Pageable pageable);
}
