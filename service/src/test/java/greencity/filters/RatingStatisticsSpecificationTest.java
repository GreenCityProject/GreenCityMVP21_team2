package greencity.filters;

import greencity.entity.RatingStatistics;
import greencity.entity.RatingStatistics_;
import greencity.entity.User;
import greencity.entity.User_;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static greencity.annotations.RatingCalculationEnum.*;
import static greencity.filters.RatingStatisticsSpecificationCriteriaBuilder.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RatingStatisticsSpecificationTest {
    private List<SearchCriteria> searchCriteriaList;
    private RatingStatisticsSpecification ratingStatisticsSpecification;

    @Mock
    private Root<RatingStatistics> root;

    @Mock
    private Predicate expected;

    @Mock
    private CriteriaQuery<RatingStatistics> criteriaQuery;

    @Mock
    private Path<Object> objectPath;

    @Mock
    private Join<RatingStatistics, User> userJoin;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @BeforeEach
    void setUp() {
        searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(getIdCriteria());

        ratingStatisticsSpecification = new RatingStatisticsSpecification(searchCriteriaList);
    }


    @Test
    void toPredicate_withNullCriteriaList_success() {
        ratingStatisticsSpecification = new RatingStatisticsSpecification(null);

        when(criteriaBuilder.conjunction()).thenReturn(expected);
        assertThatThrownBy(()->ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder))
                .isInstanceOf(NullPointerException.class);

        verify(criteriaBuilder).conjunction();
    }

    @Test
    void toPredicate_withNullType_throwNullPointer() {
        var idCriteria = searchCriteriaList.get(0);
        idCriteria.setType(null);
        when(criteriaBuilder.conjunction()).thenReturn(expected);

        assertThatThrownBy(()->ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder))
                .isInstanceOf(NullPointerException.class);

        verify(criteriaBuilder).conjunction();
    }


    @Test
    void toPredicate_withCorrectData_success() {
        addOtherCriterias();

        when(criteriaBuilder.conjunction()).thenReturn(expected);

        mockNumericPredicate(searchCriteriaList.get(0));
        mockRatingEnumPredicate(searchCriteriaList.get(1));
        mockUserIdPredicate(searchCriteriaList.get(2));
        mockUserMailPredicate(searchCriteriaList.get(3));
        mockDataRangePredicate(searchCriteriaList.get(4));
        mockNumericPredicate(searchCriteriaList.get(5));
        mockNumericPredicate(searchCriteriaList.get(6));

        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);

        var actual = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertThat(actual).isEqualTo(expected);

        verify(criteriaBuilder, times(1)).conjunction();
        verify(criteriaBuilder, times(1)).disjunction();
        verify(criteriaBuilder, times(7)).and(expected, expected);
        verify(root, times(5)).get(anyString());
    }

    private void mockNumericPredicate(SearchCriteria searchCriteria) {
        when(root.get(searchCriteria.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.equal(objectPath, searchCriteria.getValue())).thenReturn(expected);
    }

    private void mockRatingEnumPredicate(SearchCriteria ratingEnumCriteria) {
        when(criteriaBuilder.disjunction()).thenReturn(expected);
        when(root.get(ratingEnumCriteria.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.equal(objectPath, valueOf((String) ratingEnumCriteria.getValue())))
                .thenReturn(expected);
        when(criteriaBuilder.or(expected,expected)).thenReturn(expected);
    }

    private void mockUserIdPredicate(SearchCriteria userIdCriteria) {
        when(root.join(RatingStatistics_.user)).thenReturn(userJoin);
        when(userJoin.get(User_.id)).thenReturn((Path)objectPath);
        when(criteriaBuilder.equal(objectPath, userIdCriteria.getValue())).thenReturn(expected);
    }

    private void mockUserMailPredicate(SearchCriteria userMailCriteria) {
        when(root.join(RatingStatistics_.user)).thenReturn(userJoin);
        when(userJoin.get(User_.email)).thenReturn((Path)objectPath);
        when(criteriaBuilder.like(any(),eq("%" + userMailCriteria.getValue() + "%"))).thenReturn(expected);
    }

    private void mockDataRangePredicate(SearchCriteria dataRangeCriteria) {
        var dates = (String[]) dataRangeCriteria.getValue();
        var start = LocalDate.parse(dates[0]);
        var end = LocalDate.parse(dates[1]);

        var zdt1 = start.atStartOfDay(ZoneOffset.UTC);
        var zdt2 = end.atStartOfDay(ZoneOffset.UTC);


        when(root.get(dataRangeCriteria.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.between((Path)objectPath,zdt1,zdt2)).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
    }


    private void addOtherCriterias() {
        searchCriteriaList.add(getRatingEnumCriteria());
        searchCriteriaList.add(getUserIdCriteria());
        searchCriteriaList.add(getUserMailCriteria());
        searchCriteriaList.add(getDataRangeCriteria());
        searchCriteriaList.add(getPointsChangedCriteria());
        searchCriteriaList.add(getRatingCriteria());
    }
}