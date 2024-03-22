package greencity.repository;

import greencity.entity.localization.NotificationTranslation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationTranslationRepo extends JpaRepository<NotificationTranslation, Long> {

    @Query("SELECT nt FROM NotificationTranslation nt " +
        "JOIN nt.notification n JOIN n.receiver r " +
        "WHERE r.email=?1 " +
        "AND nt.language.code=?2")
    Page<NotificationTranslation> findAllNotification(String userEmail, String language, Pageable pageable);
}
