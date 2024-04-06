package greencity.filters;

import greencity.entity.Place;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class GeneralPlaceSpecification {

    public static Specification<Place> andAll(List<Specification<Place>> specifications) {
       return specifications.stream()
               .reduce(Specification::and)
               .orElse((r,c,b) -> b.conjunction());
    }
}
