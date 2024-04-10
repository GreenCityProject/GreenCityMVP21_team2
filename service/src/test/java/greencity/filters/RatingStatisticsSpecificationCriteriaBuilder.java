package greencity.filters;

import greencity.ModelUtils;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.entity.RatingStatistics_;

public class RatingStatisticsSpecificationCriteriaBuilder {
    private static final RatingStatisticsViewDto dto;

    static {
        dto = ModelUtils.getRatingStatisticsViewDto();
    }

    public static SearchCriteria getIdCriteria() {
        return SearchCriteria.builder()
                .key(RatingStatistics_.ID)
                .type(RatingStatistics_.ID)
                .value(dto.getId())
                .build();

    }

    public static SearchCriteria getRatingEnumCriteria() {
        return SearchCriteria.builder()
                .key(RatingStatistics_.RATING_CALCULATION_ENUM)
                .type("enum")
                .value(dto.getEventName())
                .build();
    }

    public static SearchCriteria getUserIdCriteria() {
        return SearchCriteria.builder()
                .key(RatingStatistics_.USER)
                .type("userId")
                .value(dto.getUserId())
                .build();
    }

    public static SearchCriteria getUserMailCriteria() {
        return SearchCriteria.builder()
                .key(RatingStatistics_.USER)
                .type("userMail")
                .value(dto.getUserEmail())
                .build();
    }

    public static SearchCriteria getDataRangeCriteria() {
        return SearchCriteria.builder()
                .key(RatingStatistics_.CREATE_DATE)
                .type("dateRange")
                .value(new String[] {dto.getStartDate(), dto.getEndDate()})
                .build();
    }

    public static SearchCriteria getPointsChangedCriteria() {
        return SearchCriteria.builder()
                .key(RatingStatistics_.POINTS_CHANGED)
                .type(RatingStatistics_.POINTS_CHANGED)
                .value(dto.getPointsChanged())
                .build();
    }

    public static SearchCriteria getRatingCriteria() {
        return SearchCriteria.builder()
                .key(RatingStatistics_.RATING)
                .type("currentRating")
                .value(dto.getCurrentRating())
                .build();
    }
}
