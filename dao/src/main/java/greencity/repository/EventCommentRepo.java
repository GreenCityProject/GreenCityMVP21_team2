package greencity.repository;

import greencity.entity.EventComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCommentRepo extends JpaRepository<EventComment, Long> {
    int countByEventId(Long eventId);

    Page<EventComment> findAllByEventIdOrderByCreatedDateDesc(Long eventId, Pageable page);

    Page<EventComment> findAllByEventParentCommentIdOrderByCreatedDateDesc(Long parentId, Pageable page);

    int countByEventParentCommentId(Long parentId);
}
