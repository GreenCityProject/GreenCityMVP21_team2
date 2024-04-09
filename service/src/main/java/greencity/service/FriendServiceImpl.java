package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import greencity.enums.FriendStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.FriendRepo;
import greencity.repository.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import greencity.exception.exceptions.CustomNotFoundException;

import java.util.List;

@Service
@Transactional
public class FriendServiceImpl implements FriendService {
    private final FriendRepo friendRepository;
    private final UserRepo userRepository;

    public FriendServiceImpl(FriendRepo friendRepository, UserRepo userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void acceptFriendRequest(long friendId) {
        User friend = userRepository.findById(friendId).orElseThrow(() -> new NotFoundException("Friend with id " + friendId + " not found"));
        User currentUser = getCurrentUser();
        friendRepository.updateFriendStatus(currentUser.getId(), friendId, FriendStatus.ACCEPTED);
    }

    @Override
    public void addNewFriend(long friendId) {
        User friend = userRepository.findById(friendId).orElseThrow(() -> new NotFoundException("Friend with id " + friendId + " not found"));
        User currentUser = getCurrentUser();
        if (friendId == currentUser.getId()) {
            throw new BadRequestException("Cannot add yourself as a friend");
        }
        if (friendRepository.existsByUserIdAndFriendsFriendId(currentUser.getId(), friendId)) {
            throw new BadRequestException("User " + friendId + " is already your friend");
        }
        friendRepository.save(new User(currentUser.getId(), friendId, FriendStatus.ACCEPTED));
    }

    @Override
    public void declineFriendRequest(long friendId) {
        User friend = userRepository.findById(friendId).orElseThrow(() -> new NotFoundException("Friend with id " + friendId + " not found"));
        User currentUser = getCurrentUser();
        if (!friendRepository.existsByUserIdAndFriendIdAndStatus(currentUser.getId(), friendId, FriendStatus.PENDING)) {
            throw new BadRequestException("Friend request from user " + friendId + " not found");
        }
        friendRepository.deleteByUserIdAndFriendIdAndStatus(currentUser.getId(), friendId, FriendStatus.PENDING);
    }

    @Override
    public void deleteUserFriend(long friendId) {
        User currentUser = getCurrentUser();
        User friend = userRepository.findById(friendId).orElseThrow(() -> new NotFoundException("Friend with id " + friendId + " not found"));
        if (!friendRepository.existsByUserIdAndFriendsFriendId(currentUser.getId(), friendId)) {
            throw new BadRequestException("User " + friendId + " is not your friend");
        }
        friendRepository.deleteByUserIdAndFriendId(currentUser.getId(), friendId);
    }


    @Override
    public PageableDto findAllFriends(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<User> friendsPage = friendRepository.findAllFriendsByUserId(currentUser.getId(), pageable);
        List<User> friends = friendsPage.getContent();
        return new PageableDto(friends, friendsPage.getTotalElements(), friendsPage.getPageable().getPageNumber(), friendsPage.getTotalPages());
    }

    @Override
    public PageableDto findAllNotFriends(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<UserFriendDto> notFriendsPage = friendRepository.findAllNotFriendsByUserId(currentUser.getId(), pageable);
        List<UserFriendDto> notFriends = notFriendsPage.getContent();
        return new PageableDto(notFriends, notFriendsPage.getTotalElements(), notFriendsPage.getPageable().getPageNumber(), notFriendsPage.getTotalPages());
    }

    @Override
    public UserManagementDto[] findUserFriends(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Page<User> friends = (Page<User>) userRepository.getAllUserFriends(userId);
        return friends.stream().map(friend -> new UserManagementDto(friend.getId(), friend.getName(), friend.getEmail())).toArray(UserManagementDto[]::new);
    }

    @Override
    public PageableDto getAllFriendRequests(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<UserFriendDto> friendRequestsPage = friendRepository.findAllFriendRequestsByFriendIdAndStatus(currentUser.getId(), FriendStatus.PENDING, pageable);
        List<UserFriendDto> friendRequests = friendRequestsPage.getContent();
        return new PageableDto(friendRequests, friendRequestsPage.getTotalElements(), friendRequestsPage.getPageable().getPageNumber(), friendRequestsPage.getTotalPages());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        } else {
            throw new CustomNotFoundException("Unable to retrieve current user");
        }
    }
}
