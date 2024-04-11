package greencity.repository;

import greencity.entity.PlaceUpdatesSubscribers;
import greencity.enums.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceUpdatesSubscribersRepo extends JpaRepository<PlaceUpdatesSubscribers, Long> {
    void deleteByUserId(Long userId);

    Optional<PlaceUpdatesSubscribers> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE PlaceUpdatesSubscribers f SET f.emailNotification = :emailNotification WHERE f.user.id = :userId")
    void updateEmailNotificationByUserId(@Param("userId") Long userId, @Param("emailNotification") EmailNotification emailNotification);

    List<PlaceUpdatesSubscribers> findAllByEmailNotification(EmailNotification emailNotification);
}
