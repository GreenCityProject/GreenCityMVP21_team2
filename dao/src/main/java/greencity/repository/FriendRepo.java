package greencity.repository;

import greencity.dto.friends.UserFriendDto;
import greencity.entity.User;
import greencity.enums.FriendStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
@Transactional
public interface FriendRepo extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id IN ( "
            + "(SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND')"
            + "UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'));")
    Page<User> findAllFriendsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT new greencity.dto.friends.UserFriendDto(u.id, u.name, u.email) " +
            "FROM User u " +
            "WHERE u.id NOT IN " +
            "(SELECT f.user.id FROM Friends f WHERE f.friend.id = :userId AND f.status = 'FRIEND')",
            countQuery = "SELECT count(u) " +
                    "FROM User u " +
                    "WHERE u.id NOT IN " +
                    "(SELECT f.user.id FROM Friends f WHERE f.friend.id = :userId AND f.status = 'FRIEND')")
    Page<UserFriendDto> findAllNotFriendsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new greencity.dto.friends.UserFriendDto(u.id, u.name, u.email) " +
            "FROM User u JOIN Friends f " +
            "WHERE f.friend.id = :friendId AND f.status = :status")
    Page<UserFriendDto> findAllFriendRequestsByFriendIdAndStatus(@Param("friendId") Long friendId,
                                                                 @Param("status") FriendStatus status,
                                                                 Pageable pageable);

    @Modifying
    @Query("UPDATE Friends f SET f.status = :friendStatus WHERE f.user.id = :userId AND f.friend.id = :friendId")
    void updateFriendStatus(@Param("userId") Long userId, @Param("friendId") Long friendId,
                            @Param("friendStatus") FriendStatus friendStatus);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Friends WHERE user_id = :userId AND friend_id = :friendId", nativeQuery = true)
    boolean existsByUserIdAndFriendsFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Friends WHERE user_id = :userId AND friend_id = :friendId AND status = :friendStatus", nativeQuery = true)
    boolean existsByUserIdAndFriendIdAndStatus(@Param("userId") Long userId,
                                               @Param("friendId") Long friendId,
                                               @Param("friendStatus") FriendStatus friendStatus);

    @Modifying
    @Query("DELETE FROM Friends f WHERE f.user.id = :userId AND f.friend.id = :friendId AND f.status = :friendStatus")
    void deleteByUserIdAndFriendIdAndStatus(@Param("userId") Long userId,
                                            @Param("friendId") Long friendId,
                                            @Param("friendStatus") FriendStatus friendStatus);

    @Modifying
    @Query("DELETE FROM Friends f WHERE f.user.id = :userId AND f.friend.id = :friendId")
    void deleteByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

}
