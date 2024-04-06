package greencity.filters;

import greencity.entity.Place;
import greencity.entity.Place_;
import greencity.enums.PlaceStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Objects.*;

import java.util.List;

@RequiredArgsConstructor
public class PlaceQueryStatusSpecification implements Specification<Place> {
    private final String searchQuery;
    private final PlaceStatus placeStatus;

    @Override
    public Predicate toPredicate(Root<Place> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var predicate = criteriaBuilder.conjunction();
        predicate = getSearchQueryPredicate(root, criteriaBuilder, predicate);
        predicate = getStatusPredicate(root, criteriaBuilder, predicate);

        return predicate;
    }

    private Predicate getStatusPredicate(Root<Place> root, CriteriaBuilder criteriaBuilder, Predicate predicate) {
        if (nonNull(placeStatus) && List.of(PlaceStatus.values()).contains(placeStatus)) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(Place_.STATUS), placeStatus));
        }
        return predicate;
    }

    private Predicate getSearchQueryPredicate(Root<Place> root, CriteriaBuilder criteriaBuilder, Predicate predicate) {
        if (nonNull(searchQuery) && !searchQuery.isBlank()) {
            var lowerCaseNameCriteria = criteriaBuilder.lower(root.get(Place_.NAME));
            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.like(lowerCaseNameCriteria, "%" + searchQuery.toLowerCase() + "%"));
        }
        return predicate;
    }
}
