package greencity.service;

import greencity.dto.specification.SpecificationNameDto;
import greencity.dto.specification.SpecificationVO;
import greencity.entity.Specification;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.SpecificationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static greencity.ModelUtils.*;
import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpecificationServiceTest {
    @Mock
    private SpecificationRepo specificationRepo;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SpecificationServiceImpl specificationService;

    private SpecificationVO specificationVO;
    private Specification specification;

    @BeforeEach
    void setUp() {
        specificationVO = getSpecificationVO();
        specification = getSpecification();
    }

    @Test
    public void save_withCorrectData_success(){
        when(modelMapper.map(specificationVO, Specification.class)).thenReturn(specification);
        when(specificationRepo.save(specification)).thenReturn(specification);
        when(modelMapper.map(specification, SpecificationVO.class)).thenReturn(specificationVO);

        var actual = specificationService.save(specificationVO);

        assertEquals(actual,specificationVO);
        verify(specificationRepo).save(specification);
    }

    @ParameterizedTest
    @CsvSource("1")
    public void findById_withCorrectData_success(Long id){
        when(specificationRepo.findById(id)).thenReturn(ofNullable(specification));
        when(modelMapper.map(specification, SpecificationVO.class)).thenReturn(specificationVO);

        var actual = specificationService.findById(id);

        assertEquals(actual,specificationVO);
        verify(specificationRepo).findById(id);
        verify(modelMapper).map(specification, SpecificationVO.class);
    }

    @ParameterizedTest
    @CsvSource("1")
    public void findById_withIncorrectId_throwNotFound(Long id){
        when(specificationRepo.findById(id)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,()-> specificationService.findById(id));
        verify(specificationRepo).findById(id);
    }

    @Test
    public void findAll_withCorrectData_success(){
        var specificationVOList = getSpecificationVOList();
        var specificationList = mapToSpecification(specificationVOList);
        var type = getType();

        when(specificationRepo.findAll()).thenReturn(specificationList);
        when(modelMapper.map(specificationList,type)).thenReturn(specificationVOList);

        var actual = specificationService.findAll();

        assertEquals(specificationVOList,actual);
        verify(specificationRepo).findAll();
        verify(modelMapper).map(specificationList,type);
    }

    @Test
    public void findAll_whenIsEmpty_expectEmptyList(){
        var type = getType();
        var emptyList = new ArrayList<Specification>();

        when(specificationRepo.findAll()).thenReturn(emptyList);
        when(modelMapper.map(emptyList, type)).thenReturn(new ArrayList<SpecificationVO>());

        var actual = specificationService.findAll();
        assertTrue(actual.isEmpty());

        verify(specificationRepo).findAll();
        verify(modelMapper).map(emptyList, type);
    }



    @ParameterizedTest
    @CsvSource({"1"})
    public void deleteById_withCorrectData_success(Long id){
        when(specificationRepo.findById(id)).thenReturn(ofNullable(specification));
        when(modelMapper.map(specification, SpecificationVO.class)).thenReturn(specificationVO);
        when(modelMapper.map(specificationVO, Specification.class)).thenReturn(specification);
        doNothing().when(specificationRepo).delete(specification);

        assertDoesNotThrow(()->specificationService.deleteById(id));
        verify(specificationRepo).delete(specification);
    }

    @ParameterizedTest
    @CsvSource({"1"})
    public void deleteById_withIncorrectId_throwNotFoundException(Long id){
        when(specificationRepo.findById(id)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,()->specificationService.deleteById(id));
        verify(specificationRepo).findById(id);
        verify(specificationRepo,never()).delete(specification);
    }


    @ParameterizedTest
    @CsvSource({"some-name"})
    public void findByName_withCorrectData_success(String name){
        when(specificationRepo.findByName(name)).thenReturn(ofNullable(specification));
        when(modelMapper.map(specification, SpecificationVO.class)).thenReturn(specificationVO);

        var actual = specificationService.findByName(name);

        assertEquals(actual,specificationVO);
        verify(specificationRepo).findByName(name);
        verify(modelMapper).map(specification, SpecificationVO.class);
    }

    @ParameterizedTest
    @CsvSource({"incorrect-name"})
    public void findByName_withIncorrectName_throwNotFound(String name){
        when(specificationRepo.findByName(name)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,()-> specificationService.findByName(name));
        verify(specificationRepo).findByName(name);
    }

    @Test
    public void findAllSpecificationDto_withCorrectData_success(){
        var specificationVOList = getSpecificationVOList();
        var specificationList = mapToSpecification(specificationVOList);
        var specificationNameDto = mapToSpecificationNameDto(specificationVO);

        when(specificationRepo.findAll()).thenReturn(specificationList);
        when(modelMapper.map(specificationList, getType())).thenReturn(specificationVOList);
        when(modelMapper.map(any(SpecificationVO.class), eq(SpecificationNameDto.class)))
                .thenReturn(specificationNameDto);

        var actual = specificationService.findAllSpecificationDto();

        assertEquals(actual.size(), specificationVOList.size());
        verify(specificationRepo).findAll();
        verify(modelMapper, times(specificationVOList.size())).map(any(SpecificationVO.class), eq(SpecificationNameDto.class));
    }

    @Test
    public void findAllSpecificationDto_whenIsEmpty_expectEmptyList(){
        var emptyList = new ArrayList<Specification>();

        when(specificationRepo.findAll()).thenReturn(emptyList);
        when(modelMapper.map(emptyList, getType())).thenReturn(new ArrayList<SpecificationVO>());

        var actual = specificationService.findAllSpecificationDto();

        assertTrue(actual.isEmpty());
        verify(specificationRepo).findAll();
        verify(modelMapper).map(emptyList, getType());
    }


    private List<Specification> mapToSpecification(List<SpecificationVO> specificationVOList){
        return specificationVOList.stream()
                .map(s -> new Specification(s.getId(),s.getName()))
                .toList();
    }

    private SpecificationNameDto mapToSpecificationNameDto(SpecificationVO specificationVO){
        return new SpecificationNameDto(specificationVO.getName());
    }

    private Type getType() {
        return new TypeToken<List<SpecificationVO>>() {
        }.getType();
    }
}