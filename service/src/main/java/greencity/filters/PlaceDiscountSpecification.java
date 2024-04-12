package greencity.filters;

import greencity.dto.filter.FilterDiscountDto;
import greencity.entity.DiscountValue_;
import greencity.entity.Place;
import greencity.entity.Place_;
import greencity.entity.Specification_;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Objects.*;

@RequiredArgsConstructor
public class PlaceDiscountSpecification implements Specification<Place> {
    private final FilterDiscountDto filterDiscount;

    @Override
    public Predicate toPredicate(Root<Place> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var predicate = criteriaBuilder.conjunction();

        if (nonNull(filterDiscount)){
            var specification = root.join(Place_.DISCOUNT_VALUES);
            var discountValuesPredicate = createDiscountValuesPredicate(criteriaBuilder, specification);
            var specificationNamePredicate = createSpecificationName(criteriaBuilder, specification);
            predicate = criteriaBuilder.and(discountValuesPredicate, specificationNamePredicate);
        }
        return predicate;
    }

    private Predicate createDiscountValuesPredicate(CriteriaBuilder criteriaBuilder, Join<Object, Object> specification) {
        var discountValues = specification.join(Specification_.DISCOUNT_VALUE);
        var graterThanCriteria = criteriaBuilder.greaterThanOrEqualTo(discountValues.get(DiscountValue_.VALUE),
                filterDiscount.getDiscountMin());
        var lessThanCriteria = criteriaBuilder.lessThanOrEqualTo(discountValues.get(DiscountValue_.VALUE),
                filterDiscount.getDiscountMax());

        return criteriaBuilder.and(graterThanCriteria,lessThanCriteria);
    }

    private Predicate createSpecificationName(CriteriaBuilder criteriaBuilder, Join<Object, Object> specification) {
        if(nonNull(filterDiscount.getSpecification())){
            var specificationName = filterDiscount.getSpecification().getName();

            if(nonNull(specificationName) && !(specificationName.isBlank())){
                return criteriaBuilder.like(specification.get(Specification_.NAME),
                        "%"+ specificationName.trim() + "%");
            }
        }
        return criteriaBuilder.conjunction();
    }
}
