package greencity.service;

import com.github.dockerjava.api.exception.UnauthorizedException;
import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import greencity.enums.FriendStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.FriendRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepo friendRepository;
    private final UserRepo userRepository;

    @Override
    public ResponseEntity<Object> acceptFriendRequest(long userId, long friendId) {
        friendRepository.updateFriendStatus(userId, friendId, FriendStatus.ACCEPTED);
        return ResponseEntity.ok().build();
    }

    @Override
    public void addNewFriend(long userId, long friendId) {
        User friend = getUserById(friendId);
        validateAddFriendOperation(userId, friendId);
        friendRepository.addNewFriend(userId, friendId);
        ResponseEntity.ok().body(new UserFriendDto(friendId, friend.getName(), friend.getEmail()));
    }

    @Override
    public ResponseEntity<Object> declineFriendRequest(long friendId) {
        User friend = getUserById(friendId);
        User currentUser = getCurrentUser();
        validateFriendRequest(currentUser.getId(), friendId, FriendStatus.PENDING);
        friendRepository.deleteByUserIdAndFriendIdAndStatus(currentUser.getId(), friendId, FriendStatus.PENDING);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> deleteUserFriend(long friendId) {
        User currentUser = getCurrentUser();
        User friend = getUserById(friendId);
        validateUserFriendship(currentUser.getId(), friendId);
        friendRepository.deleteByUserIdAndFriendId(currentUser.getId(), friendId);
        return ResponseEntity.ok().build();
    }



    @Override
    public PageableDto findAllFriends(Long userId) {
        User currentUser = getCurrentUser();
        Page<User> friendsPage = (Page<User>) friendRepository.getAllUserFriends(userId);
        return new PageableDto<>(friendsPage.getContent(), friendsPage.getTotalElements(),
                friendsPage.getNumber(), friendsPage.getTotalPages());
    }

    @Override
    public PageableDto<UserFriendDto> findAllNotFriends(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<UserFriendDto> notFriendsPage = friendRepository.findAllNotFriendsByUserId(currentUser.getId(), pageable);
        return new PageableDto<>(notFriendsPage.getContent(), notFriendsPage.getTotalElements(), notFriendsPage.getPageable().getPageNumber(), notFriendsPage.getTotalPages());
    }

    @Override
    public UserManagementDto[] findUserFriends(long userId) {
        User user = getUserById(userId);
        Page<User> friends = (Page<User>) userRepository.getAllUserFriends(userId);
        return friends.stream().map(friend -> new UserManagementDto(friend.getId(), friend.getName(), friend.getEmail())).toArray(UserManagementDto[]::new);
    }

    @Override
    public PageableDto<UserFriendDto> getAllFriendRequests(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<UserFriendDto> friendRequestsPage = friendRepository.findAllFriendRequestsByFriendIdAndStatus(currentUser.getId(), FriendStatus.PENDING, pageable);
        return new PageableDto<>(friendRequestsPage.getContent(), friendRequestsPage.getTotalElements(), friendRequestsPage.getPageable().getPageNumber(), friendRequestsPage.getTotalPages());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();


        String userEmail = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Current user not found"));
    }


    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private void validateAddFriendOperation(long userId, long friendId) {

        if (friendId == userId) {
            throw new BadRequestException("Cannot add yourself as a friend");
        }
        if (friendRepository.existsByUserIdAndFriendsFriendId(userId, friendId)) {
            throw new BadRequestException("User " + friendId + " is already your friend");
        }
    }

    private void validateFriendRequest(long userId, long friendId, FriendStatus status) {
        if (!friendRepository.existsByUserIdAndFriendIdAndStatus(userId, friendId, status)) {
            throw new BadRequestException("Friend request from user " + friendId + " not found");
        }
    }

    private void validateUserFriendship(long userId, long friendId) {
        if (!friendRepository.existsByUserIdAndFriendsFriendId(userId, friendId)) {
            throw new BadRequestException("User " + friendId + " is not your friend");
        }
    }
}