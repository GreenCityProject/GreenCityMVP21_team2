package greencity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import greencity.annotations.RatingCalculationEnum;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingStatisticsDto;
import greencity.dto.ratingstatistics.RatingStatisticsDtoForTables;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.entity.RatingStatistics;
import greencity.entity.RatingStatistics_;
import greencity.entity.User;
import greencity.filters.RatingStatisticsSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.RatingStatisticsRepo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class RatingStatisticsServiceImplTest {

    private RatingStatisticsRepo ratingStatisticsRepo = mock(RatingStatisticsRepo.class);
    private ModelMapper modelMapper = new ModelMapper();
    private RatingStatisticsServiceImpl ratingStatisticsService =
            new RatingStatisticsServiceImpl(ratingStatisticsRepo, modelMapper);
    private ZonedDateTime currentDate = ZonedDateTime.now();

    private User user = new User();
    private RatingStatistics ratingStatistics = new RatingStatistics(15L, currentDate, RatingCalculationEnum.LIKE_COMMENT,
            12D, 85D, user);
    private RatingStatisticsDto ratingStatisticsDto = modelMapper.map(ratingStatistics, RatingStatisticsDto.class);
    private RatingStatisticsViewDto ratingStatisticsViewDto = new RatingStatisticsViewDto("15", "EventName",
            "48", "UserEmail", currentDate.toString(), currentDate.plusMonths(1).toString(),
            "12", "85");
    private Pageable pageable = mock(Pageable.class);
    private PageableAdvancedDto<RatingStatisticsDtoForTables> ratingStatisticsDtoForTablesPageableAdvancedDto =
            new PageableAdvancedDto<>(Collections.emptyList(),0,0,1,0,
                    false, false, true, true);
    private List<RatingStatistics> listgetRatingStatistics = Arrays.asList(ratingStatistics, ratingStatistics);
    private List<RatingStatisticsDto> ratingStatisticsDtoList = Arrays.asList(ratingStatisticsDto, ratingStatisticsDto);

    @Test
    void save_RatingStatisticsServiceImplTest_ShouldSaveRatingStatisticsAndReturnVO() {
        RatingStatisticsVO expected = modelMapper.map(ratingStatistics, RatingStatisticsVO.class);

        when(ratingStatisticsRepo.save(any())).thenReturn(ratingStatistics);

        RatingStatisticsVO actual = ratingStatisticsService.save(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCreateDate(), actual.getCreateDate());
        verify(ratingStatisticsRepo, times(1)).save(any());
    }

    @ParameterizedTest
    @NullSource
    void save_RatingStatisticsServiceImplTestWithNullSource_ShouldReturnIllegalArgumentException(
            RatingStatisticsVO nullRatingStatisticsVO) {
        assertThrows(IllegalArgumentException.class, () -> {
            ratingStatisticsService.save(nullRatingStatisticsVO);
        });
    }

    @Test
    void getRatingStatisticsForManagementByPage_RatingStatisticsServiceImplTest_ShouldReturnPageableAdvancedDto() {
        when(ratingStatisticsRepo.findAll(any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageableAnswer = invocation.getArgument(0);
                    List<RatingStatistics> content = Collections.emptyList();
                    return new PageImpl<>(content, pageableAnswer, content.size());
                });


        PageableAdvancedDto<RatingStatisticsDtoForTables> expected = ratingStatisticsDtoForTablesPageableAdvancedDto;

        PageableAdvancedDto<RatingStatisticsDtoForTables> actual =
                ratingStatisticsService.getRatingStatisticsForManagementByPage(pageable);

        assertEquals(expected, actual);
        verify(ratingStatisticsRepo, times(1)).findAll(any(Pageable.class));
    }

    @ParameterizedTest
    @NullSource
    void getRatingStatisticsForManagementByPage_RatingStatisticsServiceImplTestWithNullSource_ShouldReturnNullPointerException(
            Pageable nullPageable) {
        assertThrows(NullPointerException.class, () -> {
            ratingStatisticsService.getRatingStatisticsForManagementByPage(nullPageable);
        });
    }

    @Test
    void getAllRatingStatistics_RatingStatisticsServiceImplTest_ShouldReturnListOfRatingStatisticsDto() {

        when(ratingStatisticsRepo.findAll()).thenReturn(listgetRatingStatistics);

        List<RatingStatisticsDto> expected = ratingStatisticsDtoList;
        List<RatingStatisticsDto> actual = ratingStatisticsService.getAllRatingStatistics();

        assertEquals(expected, actual);
        verify(ratingStatisticsRepo, times(1)).findAll();
    }

    @Test
    void getFilteredRatingStatisticsForExcel_ShouldReturnListOfRatingStatisticsDto() {
        when(ratingStatisticsRepo.findAll(any(RatingStatisticsSpecification.class))).thenReturn(listgetRatingStatistics);

        List<RatingStatisticsDto> expected = ratingStatisticsDtoList;
        List<RatingStatisticsDto> actual =
                ratingStatisticsService.getFilteredRatingStatisticsForExcel(ratingStatisticsViewDto);

        assertEquals(expected, actual);
        verify(ratingStatisticsRepo, times(1)).findAll(any(RatingStatisticsSpecification.class));
    }

    @ParameterizedTest
    @NullSource
    void getFilteredRatingStatisticsForExcel_RatingStatisticsServiceImplTestWithNullSource_ShouldReturnNullPointerException(
            RatingStatisticsViewDto nullRatingStatisticsViewDto) {
        assertThrows(NullPointerException.class, () -> {
            ratingStatisticsService.getFilteredRatingStatisticsForExcel(nullRatingStatisticsViewDto);
        });
    }

    @Test
    void getFilteredDataForManagementByPage_RatingStatisticsServiceImplTest_ShouldReturnPageableAdvancedDto() {
        when(ratingStatisticsRepo.findAll(any(RatingStatisticsSpecification.class), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageableAnswer = invocation.getArgument(1);
                    List<RatingStatistics> content = Collections.emptyList();
                    return new PageImpl<>(content, pageableAnswer, content.size());
                });

        PageableAdvancedDto<RatingStatisticsDtoForTables> expected = ratingStatisticsDtoForTablesPageableAdvancedDto;
        PageableAdvancedDto<RatingStatisticsDtoForTables> actual =
                ratingStatisticsService.getFilteredDataForManagementByPage(pageable, ratingStatisticsViewDto);

        assertEquals(expected, actual);
        verify(ratingStatisticsRepo, times(1)).findAll(any(RatingStatisticsSpecification.class), any(Pageable.class));
    }

    @ParameterizedTest
    @NullSource
    void getFilteredDataForManagementByPage_RatingStatisticsServiceImplTestWithNullSource_ShouldReturnNullPointerException(
            Pageable nullPageable) {
        RatingStatisticsViewDto nullRatingStatisticsViewDto = null;
        assertThrows(NullPointerException.class, () -> {
            ratingStatisticsService.getFilteredDataForManagementByPage(nullPageable,nullRatingStatisticsViewDto);
        });
    }

    @Test
    void buildSearchCriteria_RatingStatisticsServiceImplTest_ShouldReturnListOfSearchCriteria() {
        List<SearchCriteria> expectedList = new ArrayList<>();
        expectedList.add(SearchCriteria.builder()
                .key(RatingStatistics_.ID)
                .type(RatingStatistics_.ID)
                .value("15")
                .build());
        expectedList.add(SearchCriteria.builder()
                .key(RatingStatistics_.RATING_CALCULATION_ENUM)
                .type("enum")
                .value("EventName")
                .build());
        expectedList.add(SearchCriteria.builder()
                .key(RatingStatistics_.USER)
                .type("userId")
                .value("48")
                .build());
        expectedList.add(SearchCriteria.builder()
                .key(RatingStatistics_.USER)
                .type("userMail")
                .value("UserEmail")
                .build());
        expectedList.add(SearchCriteria.builder()
                .key(RatingStatistics_.CREATE_DATE)
                .type("dateRange")
                .value(new String[] {ratingStatisticsViewDto.getStartDate(), ratingStatisticsViewDto.getEndDate()})
                .build());
        expectedList.add(SearchCriteria.builder()
                .key(RatingStatistics_.POINTS_CHANGED)
                .type(RatingStatistics_.POINTS_CHANGED)
                .value("12")
                .build());
        expectedList.add(SearchCriteria.builder()
                .key(RatingStatistics_.RATING)
                .type("currentRating")
                .value("85")
                .build());

        List<SearchCriteria> actualList = ratingStatisticsService.buildSearchCriteria(ratingStatisticsViewDto);

        assertEquals(expectedList, actualList);
    }

    @ParameterizedTest
    @NullSource
    void buildSearchCriteria_RatingStatisticsServiceImplTestWithNullSource_ShouldReturnNullPointerException(
            RatingStatisticsViewDto nullRatingStatisticsViewDto) {
        assertThrows(NullPointerException.class, () -> {
            ratingStatisticsService.buildSearchCriteria(nullRatingStatisticsViewDto);
        });
    }
}