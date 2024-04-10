package greencity.repository;

import greencity.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationRepo extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n JOIN FETCH n.receiver where n.id=?1")
    Optional<Notification> findByIdFetchUser(Long id);

    @Query("SELECT n FROM Notification n JOIN FETCH n.evaluator WHERE n.receiver.id=?1")
    Page<Notification> findAllByReceiverId(Long receiverId, Pageable pageable);


    @Query("SELECT n FROM Notification n JOIN FETCH n.evaluator " +
            "WHERE n.receiver.id=?1 " +
            "AND n.status = 'UNREAD'")
    Page<Notification> findAllUnreadByReceiverId(Long receiverId, Pageable pageable);


}
