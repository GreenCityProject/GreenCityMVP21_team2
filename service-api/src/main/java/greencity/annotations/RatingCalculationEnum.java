package greencity.annotations;

import lombok.Getter;

@Getter
public enum RatingCalculationEnum {
    ADD_COMMENT(2),
    DELETE_COMMENT(-2),
    LIKE_COMMENT(1),
    UNLIKE_COMMENT(-1),
    ADD_ECO_NEWS(20),
    DELETE_ECO_NEWS(-20),
    EVENTS_RATING_1(-2),
    EVENTS_RATING_2(1),
    EVENTS_RATING_3(2);

    private final float ratingPoints;

    RatingCalculationEnum(float ratingPoints) {
        this.ratingPoints = ratingPoints;
    }
}
