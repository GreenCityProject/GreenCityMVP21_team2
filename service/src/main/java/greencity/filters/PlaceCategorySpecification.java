package greencity.filters;

import greencity.entity.Category_;
import greencity.entity.Place;
import greencity.entity.Place_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;


@RequiredArgsConstructor
public class PlaceCategorySpecification implements Specification<Place> {
    private final String[] categories;

    @Override
    public Predicate toPredicate(Root<Place> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (Objects.isNull(categories)|| categories.length == 0){
            return criteriaBuilder.conjunction();
        }
        var categoriesJoin = root.join(Place_.CATEGORY);
        var nameInCriteria = categoriesJoin.get(Category_.NAME).in((Object[]) categories);
        var nameUaInCriteria = categoriesJoin.get(Category_.NAME_UA).in((Object[]) categories);

        return criteriaBuilder.or(nameInCriteria, nameUaInCriteria);
    }
}
