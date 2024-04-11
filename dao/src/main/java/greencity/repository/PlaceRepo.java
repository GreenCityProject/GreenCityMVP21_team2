package greencity.repository;

import greencity.entity.Place;
import greencity.entity.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepo extends JpaRepository<Place, Long>, JpaSpecificationExecutor<Place> {

    List<Place> findAllByName(String name);

}
