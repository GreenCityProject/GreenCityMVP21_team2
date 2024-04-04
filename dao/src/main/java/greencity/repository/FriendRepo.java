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
    boolean existsByUserIdAndFriendsFriendId(Long userId, Long friendId);

    boolean existsByUserIdAndFriendsFriendIdAndFriendsStatus(Long userId, Long friendId, FriendStatus status);

    @Query("SELECT new greencity.dto.friends.UserFriendDto(u.id, u.name, u.email) "
            + "FROM User u JOIN u.friends f WHERE f.user.id = :userId AND f.status = 'FRIEND'")
    Page<UserFriendDto> findAllFriendsByUserId(@Param("userId") Long userId);

    @Query("SELECT new greencity.dto.friends.UserFriendDto(u.id, u.name, u.email) "
            + "FROM User u WHERE u.id NOT IN "
            + "(SELECT f.friend.id FROM Friends f WHERE f.user.id = :userId AND f.status = 'FRIEND')")
    Page<UserFriendDto> findAllNotFriendsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new greencity.dto.friends.UserFriendDto(u.id, u.name, u.email) "
            + "FROM User u JOIN u.friends f WHERE f.friend.id = :friendId AND f.status = 'PENDING'")
    Page<UserFriendDto> findAllFriendRequestsByFriendIdAndStatus(@Param("friendId") Long friendId,
                                                                 @Param("status") FriendStatus status,
                                                                 Pageable pageable);

    @Modifying
    @Query("UPDATE Friends f SET f.status = :friendStatus WHERE f.user.id = :userId AND f.friend.id = :friendId")
    void updateFriendStatus(@Param("userId") Long userId, @Param("friendId") Long friendId,
                            @Param("friendStatus") FriendStatus friendStatus);

    boolean existsByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    boolean existsByUserIdAndFriendIdAndStatus(@Param("userId") Long userId, @Param("friendId") Long friendId,
                                               @Param("friendStatus") FriendStatus friendStatus);

    void deleteByUserIdAndFriendIdAndStatus(@Param("userId") Long userId, @Param("friendId") Long friendId,
                                            @Param("friendStatus") FriendStatus friendStatus);

    void deleteByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
