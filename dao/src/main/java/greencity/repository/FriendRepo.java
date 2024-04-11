package greencity.repository;

import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.entity.Friends;
import greencity.entity.User;
import greencity.enums.FriendStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface FriendRepo extends JpaRepository<User, Long> {
    @Query(value = "SELECT new greencity.dto.friends.UserFriendDto(u.id, u.name, u.email) " + "FROM User u " + "WHERE u.id NOT IN " + "(SELECT f.user.id FROM Friends f WHERE f.friend.id = :userId AND f.status = 'FRIEND')", countQuery = "SELECT count(u) " + "FROM User u " + "WHERE u.id NOT IN " + "(SELECT f.user.id FROM Friends f WHERE f.friend.id = :userId AND f.status = 'FRIEND')")
    Page<UserFriendDto> findAllNotFriendsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new greencity.dto.friends.UserFriendDto(u.id, u.name, u.email) " + "FROM User u JOIN Friends f " + "WHERE f.friend.id = :friendId AND f.status = :status")
    Page<UserFriendDto> findAllFriendRequestsByFriendIdAndStatus(@Param("friendId") Long friendId, @Param("status") FriendStatus status, Pageable pageable);

    @Modifying
    @Query(nativeQuery = true,
            value = "INSERT INTO users_friends(user_id, friend_id, status, created_date) "
                    + "VALUES (:userId, :friendId, :friendStatus, CURRENT_TIMESTAMP) "
                    + "ON CONFLICT (user_id, friend_id) DO UPDATE SET status = :friendStatus")
    void updateFriendStatus(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("friendStatus") FriendStatus friendStatus);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " + "FROM users_friends WHERE user_id = :userId AND friend_id = :friendId", nativeQuery = true)
    boolean existsByUserIdAndFriendsFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " + "FROM users_friends WHERE user_id = :userId AND friend_id = :friendId AND status = :friendStatus", nativeQuery = true)
    boolean existsByUserIdAndFriendIdAndStatus(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("friendStatus") FriendStatus friendStatus);

    @Modifying
    @Query("DELETE FROM Friends f WHERE f.user.id = :userId AND f.friend.id = :friendId AND f.status = :friendStatus")
    void deleteByUserIdAndFriendIdAndStatus(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("friendStatus") FriendStatus friendStatus);

    @Modifying
    @Query("DELETE FROM Friends f WHERE f.user.id = :userId AND f.friend.id = :friendId")
    void deleteByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Modifying
    @Query(nativeQuery = true,
            value = "INSERT INTO users_friends(user_id, friend_id, status, created_date) "
                    + "VALUES (:userId, :friendId, 'REQUEST', CURRENT_TIMESTAMP) "
                    + "ON CONFLICT (user_id, friend_id) DO UPDATE SET status = 'REQUEST'")
    void addNewFriend(Long userId, Long friendId);

    @Query("SELECT f FROM Friends f " +
            "WHERE (f.friend.id = :userId OR f.user.id = :userId) AND f.status = 'FRIEND'")
    Page<Friends> getAllUserFriends(@Param("userId") Long userId, Pageable pageable);
}
