package greencity.filters;

import static java.time.LocalDate.*;
import static java.util.Objects.*;

import greencity.entity.BreakTime_;
import greencity.entity.OpeningHours_;
import greencity.entity.Place;
import greencity.entity.Place_;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalTime;

@RequiredArgsConstructor
public class OpeningHoursPlaceSpecification implements Specification<Place> {
    private final LocalTime time;

    @Override
    public Predicate toPredicate(Root<Place> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (isNull(time)) {
            return criteriaBuilder.conjunction();
        }
        query.distinct(true);
        var openingHoursJoin = root.join(Place_.OPENING_HOURS);

        var hoursPredicate = createHoursPredicate(openingHoursJoin,criteriaBuilder);
        var breakTimeCriteria = createBreakTimePredicate(criteriaBuilder, openingHoursJoin);

        return criteriaBuilder.or(hoursPredicate, breakTimeCriteria);
    }

    private Predicate createHoursPredicate(Join<Object, Object> openingHoursJoin, CriteriaBuilder criteriaBuilder) {
        var currentDayPredicate = criteriaBuilder.equal(openingHoursJoin.get(OpeningHours_.WEEK_DAY), now().getDayOfWeek().ordinal());
        var startPredicate = criteriaBuilder.lessThanOrEqualTo(openingHoursJoin.get(OpeningHours_.OPEN_TIME), time);
        var endPredicate = criteriaBuilder.greaterThanOrEqualTo(openingHoursJoin.get(OpeningHours_.CLOSE_TIME), time);

        return criteriaBuilder.and(currentDayPredicate, startPredicate, endPredicate);
    }

    private Predicate createBreakTimePredicate(CriteriaBuilder criteriaBuilder, Join<Object, Object> openingHoursJoin) {
        var breakTimeJoin = openingHoursJoin.join(OpeningHours_.BREAK_TIME, JoinType.LEFT);

        var breakStartPredicate = criteriaBuilder.greaterThan(breakTimeJoin.get(BreakTime_.START_TIME), time);
        var breakEndPredicate = criteriaBuilder.lessThan(breakTimeJoin.get(BreakTime_.END_TIME), time);

        return criteriaBuilder.or(breakStartPredicate, breakEndPredicate);
    }

}
