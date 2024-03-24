package greencity.repository;

import greencity.entity.localization.NotificationTranslation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationTranslationRepo extends JpaRepository<NotificationTranslation, Long> {

    @Query("SELECT nt FROM NotificationTranslation nt " +
        "JOIN FETCH nt.notification n  JOIN n.receiver r " +
        "WHERE r.email=?1 " +
        "AND nt.language.code=?2")
    Page<NotificationTranslation> findAll(String userEmail, String language, Pageable pageable);

    @Query("SELECT nt FROM NotificationTranslation nt " +
        "JOIN FETCH nt.notification n JOIN n.receiver r " +
        "WHERE r.email=?1 " +
        "AND nt.language.code=?2 AND n.status ='UNREAD'")
    Page<NotificationTranslation> findAllWithUnreadStatus(String userEmail, String language, Pageable pageable);


    @Query("SELECT nt FROM NotificationTranslation nt " +
            "JOIN FETCH nt.notification n JOIN FETCH n.receiver r " +
            "WHERE n.id=?1 " +
            "AND nt.language.code=?2")
    Optional<NotificationTranslation> findByNotificationIdAndLanguage(Long notificationId, String language);
}
