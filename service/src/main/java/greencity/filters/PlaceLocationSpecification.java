package greencity.filters;

import static java.util.Objects.*;

import greencity.dto.place.MapBoundsDto;
import greencity.entity.Place;
import greencity.entity.PlaceLocations_;
import greencity.entity.Place_;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class PlaceLocationSpecification implements Specification<Place> {
    private final MapBoundsDto mapBoundsDto;

    @Override
    public Predicate toPredicate(Root<Place> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (checkAreBoundsNotNull(mapBoundsDto))
            return criteriaBuilder.conjunction();

        var locationJoin = root.join(Place_.LOCATION);
        
        var withinLatBounds = creteLatBoundsPredicate(criteriaBuilder, locationJoin);
        var withinLngBounds = createLngBoundsPredicate(criteriaBuilder, locationJoin);
        return criteriaBuilder.and(withinLatBounds, withinLngBounds);
    }

    private boolean checkAreBoundsNotNull(MapBoundsDto bounds) {
        return isNull(bounds);
    }

    private Predicate creteLatBoundsPredicate(CriteriaBuilder criteriaBuilder, Join<Object, Object> locationJoin) {
        return criteriaBuilder.between(locationJoin.get(PlaceLocations_.LAT),
                mapBoundsDto.getSouthWestLat(), mapBoundsDto.getNorthEastLat());
    }

    private Predicate createLngBoundsPredicate(CriteriaBuilder criteriaBuilder, Join<Object, Object> locationJoin) {
        return criteriaBuilder.between(locationJoin.get(PlaceLocations_.LNG),
                mapBoundsDto.getSouthWestLng(), mapBoundsDto.getNorthEastLng());
    }
}
