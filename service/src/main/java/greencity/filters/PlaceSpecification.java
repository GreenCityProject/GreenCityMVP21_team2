package greencity.filters;

import greencity.entity.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class PlaceSpecification implements MySpecification<Place>{
    private transient List<SearchCriteria> searchCriteriaList;
    @Override
    public Predicate toPredicate(Root<Place> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getKey().equals("discountValues")) {
                if (searchCriteria.getType().equals("name")) {
                    allPredicates =
                            criteriaBuilder.and(allPredicates, getDiscountSpecificationValues(root, criteriaBuilder, searchCriteria));
                }
                if (searchCriteria.getType().equals("discountRange")){
                    allPredicates =
                            criteriaBuilder.and(allPredicates, getDiscountRangeValues(root, criteriaBuilder, searchCriteria));
                }
            }
        }
        return allPredicates;
    }

    private Predicate getDiscountRangeValues(Root<Place> root, CriteriaBuilder criteriaBuilder,
                                             SearchCriteria searchCriteria) {
        try {
            Object value = searchCriteria.getValue();
            if (value instanceof String[]) {
                String[] discounts = (String[]) value;
                Integer discountMin = Integer.valueOf(discounts[0]);
                Integer discountMax = Integer.valueOf(discounts[1]);

                return criteriaBuilder.between(root.join(Place_.discountValues)
                        .join(Specification_.discountValue)
                        .get(DiscountValue_.value), discountMin, discountMax);
            } else {
                return value.toString().trim().equals("") ? criteriaBuilder.conjunction()
                        : criteriaBuilder.disjunction();
            }
        } catch (NumberFormatException ex) {
            return criteriaBuilder.conjunction();
        }
    }

    private Predicate getDiscountSpecificationValues(Root<Place> root, CriteriaBuilder criteriaBuilder,
                                             SearchCriteria searchCriteria) {
        return criteriaBuilder.like(root.join(Place_.discountValues)
                        .get(Specification_.name),
                "%" + searchCriteria.getValue() + "%");
    }
}
