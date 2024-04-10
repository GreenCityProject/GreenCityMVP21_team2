package greencity.filters;

import greencity.entity.*;
import greencity.entity.localization.TagTranslation_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Component
@Scope("prototype")
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class EventSpecification implements MySpecification<Events>{
    private transient List<SearchCriteria> searchCriteriaList;

    @Override
    public Predicate toPredicate(Root<Events> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("open")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getOpenPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("eventAttender")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getAttenderPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("eventsFollowers")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getFollowersPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("organizer")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getOrganizerPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("tags")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getTagsPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("finishDate")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getFinishDatePredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("startDate")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getStartDatePredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("onlineLink")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getOnlineLinkPredicate(root, criteriaBuilder, searchCriteria));
            }

        }
        return allPredicates;
    }

    private Predicate getTagsPredicate(Root<Events> root, CriteriaBuilder criteriaBuilder,
                                       SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(
                root.join(Events_.tags).join(Tag_.tagTranslations).get(TagTranslation_.name).as(String.class),
                "%" + searchCriteria.getValue() + "%");
    }

    private Predicate getOpenPredicate(Root<Events> root, CriteriaBuilder criteriaBuilder,
                                       SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(
                root.get(Events_.open).as(String.class),
                "%" + searchCriteria.getValue() + "%");
    }

    private Predicate getFinishDatePredicate(Root<Events> root, CriteriaBuilder criteriaBuilder,
                                       SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }

        ZonedDateTime finishDate = ZonedDateTime.parse(searchCriteria.getValue().toString());
        return criteriaBuilder.lessThan(
                root.join(Events_.datesLocations).get(EventDateLocation_.finishDate),
                finishDate);
    }

    private Predicate getStartDatePredicate(Root<Events> root, CriteriaBuilder criteriaBuilder,
                                       SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        ZonedDateTime startDate = ZonedDateTime.parse(searchCriteria.getValue().toString());
        return criteriaBuilder.greaterThan(
                root.join(Events_.datesLocations).get(EventDateLocation_.startDate),
                startDate);
    }

    private Predicate getOnlineLinkPredicate(Root<Events> root, CriteriaBuilder criteriaBuilder,
                                       SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.isNotNull(
                root.join(Events_.datesLocations).get(EventDateLocation_.onlineLink));
    }

    private Predicate getAttenderPredicate(Root<Events> root, CriteriaBuilder criteriaBuilder,
                                       SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(
                root.join(Events_.eventAttender).get(User_.email).as(String.class),
                "%" + searchCriteria.getValue() + "%");
    }

    private Predicate getFollowersPredicate(Root<Events> root, CriteriaBuilder criteriaBuilder,
                                       SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(
                root.join(Events_.eventsFollowers).get(User_.email).as(String.class),
                "%" + searchCriteria.getValue() + "%");
    }

    private Predicate getOrganizerPredicate(Root<Events> root, CriteriaBuilder criteriaBuilder,
                                       SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(
                root.join(Events_.organizer).get(User_.email).as(String.class),
                "%" + searchCriteria.getValue() + "%");
    }
}
