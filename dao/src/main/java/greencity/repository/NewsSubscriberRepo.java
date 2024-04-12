package greencity.repository;

import greencity.entity.NewsSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsSubscriberRepo extends JpaRepository<NewsSubscriber, Long> {
    List<NewsSubscriber> findAll();

    void deleteByEmail(String email);

    Optional<NewsSubscriber> findByEmail(String email);
}
