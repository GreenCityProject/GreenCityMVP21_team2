package greencity.repository;

import greencity.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepo extends JpaRepository<Place, Long>, JpaSpecificationExecutor<Place> {
    List<Place> findByFavoritesId(Long userId);
}
