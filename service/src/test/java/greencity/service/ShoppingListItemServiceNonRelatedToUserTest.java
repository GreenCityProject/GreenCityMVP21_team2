package greencity.service;

import static greencity.ModelUtils.*;
import static java.util.Collections.*;
import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.shoppinglistitem.*;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.exception.exceptions.*;
import greencity.filters.ShoppingListItemSpecification;
import greencity.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
public class ShoppingListItemServiceNonRelatedToUserTest {
    @Mock
    private ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;
    @Mock
    private ShoppingListItemRepo shoppingListItemRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserShoppingListItemRepo userShoppingListItemRepo;
    @Mock
    private HabitAssignRepo habitAssignRepo;

    @InjectMocks
    private ShoppingListItemServiceImpl shoppingListItemService;

    private ShoppingListItem shoppingListItem;


    @BeforeEach
    void setUp() {
        shoppingListItem = getShoppingListItem();
    }

    @Test
    public void findAll_whenNoThrownException_expectSuccess(){
        var languageConde = AppConstant.DEFAULT_LANGUAGE_CODE;
        var shoppingListItems = getShoppingListItemTranslations();

        when(shoppingListItemTranslationRepo.findAllByLanguageCode(languageConde))
                .thenReturn(shoppingListItems);
        when(modelMapper.map(any(ShoppingListItemTranslation.class), eq(ShoppingListItemDto.class)))
                .thenReturn(any(ShoppingListItemDto.class));

        var actual = shoppingListItemService.findAll(languageConde);

        assertEquals(shoppingListItems.size(), actual.size());
        verifyFindAllByLanguageCodeMethodCall();
        verify(modelMapper, times(shoppingListItems.size())).map(any(),any());
    }

    @Test
    public void findAll_whenDoesNotExist_expectEmptyList(){
        var languageConde = AppConstant.DEFAULT_LANGUAGE_CODE;

        when(shoppingListItemTranslationRepo.findAllByLanguageCode(languageConde))
                .thenReturn(new ArrayList<>());

        var actual = shoppingListItemService.findAll(languageConde);

        assertTrue(actual.isEmpty());
        verifyFindAllByLanguageCodeMethodCall();
    }

    @Test
    public void saveShoppingListItem_withNonNullParameter_expectSuccess(){
        var post = getShoppingListItemPostDto();
        shoppingListItem.setTranslations(Arrays.asList(new ShoppingListItemTranslation(),
                new ShoppingListItemTranslation()));
        var type = getType();

        when(modelMapper.map(post,ShoppingListItem.class)).thenReturn(shoppingListItem);
        when(shoppingListItemRepo.save(shoppingListItem)).thenReturn(shoppingListItem);
        when(modelMapper.map(shoppingListItem.getTranslations(),type)).thenReturn(shoppingListItem.getTranslations());

        shoppingListItemService.saveShoppingListItem(post);

        verifyModelMapperMapMethodCall();
        verify(shoppingListItemRepo).save(shoppingListItem);
        verifyModelMapperMapWithTypeMethodCall(type);
    }

    @ParameterizedTest
    @NullSource
    public void update_withNullParameter_throwNotFound(ShoppingListItemPostDto item){
        assertThrows(NullPointerException.class,
                ()-> shoppingListItemService.update(item));
    }

    @Test
    public void update_whenItemIsNotPresent_throwNotFound(){
        var post = getShoppingListItemPostDto();

        when(shoppingListItemRepo.findById(anyLong())).thenReturn(empty());

        assertThrows(ShoppingListItemNotFoundException.class, ()-> shoppingListItemService.update(post));
        verifyItemRepoFindByIdMethodCall();
    }

    @Test
    public void update_withNoParameter_expectSuccess(){
        var post = getShoppingListItemPostDto();
        post.setTranslations(getLanguageTranslationsDTOs());

        when(shoppingListItemRepo.findById(shoppingListItem.getId())).thenReturn(of(shoppingListItem));
        when(shoppingListItemRepo.save(shoppingListItem)).thenReturn(shoppingListItem);
        when(modelMapper.map(shoppingListItem.getTranslations(), getType()))
                .thenReturn(post.getTranslations());

        var actual = shoppingListItemService.update(post);

        assertEquals(post.getTranslations(), actual);
        verify(shoppingListItemRepo).save(shoppingListItem);
        verifyModelMapperMapWithTypeMethodCall(getType());
    }

    @Test
    public void findShoppingListItemById_withNoElementsFounded_throwNotFound(){
        when(shoppingListItemRepo.findById(anyLong())).thenReturn(empty());

        assertThrows(ShoppingListItemNotFoundException.class,
                ()-> shoppingListItemService.findShoppingListItemById(anyLong()));
        verifyItemRepoFindByIdMethodCall();
    }

    @Test
    public void findShoppingListItemById_withNoExceptionThrown_expectSuccess(){
        var response = new ShoppingListItemResponseDto();

        when(shoppingListItemRepo.findById(anyLong())).thenReturn(of(shoppingListItem));
        when(modelMapper.map(shoppingListItem, ShoppingListItemResponseDto.class))
                .thenReturn(response);

        var actual = shoppingListItemService.findShoppingListItemById(anyLong());

        assertNotNull(actual);
        verifyItemRepoFindByIdMethodCall();
        verifyModelMapperMapMethodCall();
    }

    private void verifyItemRepoFindByIdMethodCall() {
        verify(shoppingListItemRepo).findById(anyLong());
    }

    @Test
    public void delete_withNoException_expectSuccess(){
        long id = 1L;
        doNothing().when(shoppingListItemRepo).deleteById(id);

        assertEquals(id, shoppingListItemService.delete(id));
        verifyDeleteByIdMethodCall(1);
    }

    @ParameterizedTest
    @CsvSource({"1"})
    public void delete_withNonExistentId_throwNotDeletedException(Long id){
        doThrow(EmptyResultDataAccessException.class).when(shoppingListItemRepo).deleteById(id);

        assertThrows(NotDeletedException.class, ()-> shoppingListItemService.delete(id));
        verifyDeleteByIdMethodCall(1);
    }

    @Test
    public void findShoppingListItemsForManagementByPage_withCorrectParameter_expectSuccess(){
        assertServiceMethodHasCorrectFlow(ShoppingListItemServiceImpl::findShoppingListItemsForManagementByPage,
                PagingAndSortingRepository::findAll);
    }

    @Test
    public void searchBy_withCorrectParameters_expectSuccess(){
        var query = "query";
        assertServiceMethodHasCorrectFlow((service, page)-> service.searchBy(page,query),
                (repo, page) -> repo.searchBy(eq(page),anyString()));

    }

    @Test
    public void getFilteredDataForManagementByPage_withCorrectParameter_expectSuccess(){
        var listItemViewDto = new ShoppingListItemViewDto();
        assertServiceMethodHasCorrectFlow((service, page)-> service.getFilteredDataForManagementByPage(page,listItemViewDto),
                (repo, page) -> repo.findAll(any(ShoppingListItemSpecification.class),eq(page)));
    }

    private void assertServiceMethodHasCorrectFlow(BiFunction <ShoppingListItemServiceImpl, PageRequest,
            PageableAdvancedDto<ShoppingListItemManagementDto>> serviceFunc,
                                                      BiFunction<ShoppingListItemRepo,PageRequest, Page<ShoppingListItem>> repoFunc){
        var pageable = PageRequest.of(1,1);
        var shoppingListItemPage = createShoppingListItemPage(pageable, shoppingListItem);
        var itemManagementDto = getShoppingListItemManagementDto();

        when(repoFunc.apply(shoppingListItemRepo,pageable)).thenReturn(shoppingListItemPage);
        when(modelMapper.map(shoppingListItem,ShoppingListItemManagementDto.class)).thenReturn(itemManagementDto);

        var actual = serviceFunc.apply(shoppingListItemService,pageable);

        assertPagePropertyEquals(shoppingListItemPage, actual);
        assertPageContentEquals(itemManagementDto, actual);
        verifyModelMapperMapMethodCall();
    }

    private void assertPagePropertyEquals(Page<ShoppingListItem> shoppingListItemPage, PageableAdvancedDto<ShoppingListItemManagementDto> actual) {
        assertEquals(shoppingListItemPage.getNumber(), actual.getNumber());
        assertEquals(shoppingListItemPage.getTotalPages(), actual.getTotalPages());
        assertEquals(shoppingListItemPage.isFirst(), actual.isFirst());
        assertEquals(shoppingListItemPage.isLast(), actual.isLast());
    }

    private void assertPageContentEquals(ShoppingListItemManagementDto itemManagementDto, PageableAdvancedDto<ShoppingListItemManagementDto> actual) {
        assertEquals(itemManagementDto, actual.getPage().get(0));
    }

    private Page<ShoppingListItem> createShoppingListItemPage(PageRequest pageable, ShoppingListItem item) {
        return new PageImpl<>(singletonList(item), pageable, 1L);
    }

    private ShoppingListItemManagementDto getShoppingListItemManagementDto() {
        var itemManagementDto = new ShoppingListItemManagementDto();
        itemManagementDto.setId(1L);
        itemManagementDto.setTranslations(singletonList(new ShoppingListItemTranslationVO()));
        return itemManagementDto;
    }


    @ParameterizedTest
    @MethodSource("idsProvider")
    public void deleteAll_withCorrectParameter_expectSuccess(List<Long> ids){
        doNothing().when(shoppingListItemRepo).deleteById(anyLong());

        var actualIds = shoppingListItemService.deleteAllShoppingListItemsByListOfId(ids);

        assertEquals(ids,actualIds);
        verifyDeleteByIdMethodCall(ids.size());
    }

    @ParameterizedTest
    @MethodSource("idsProvider")
    public void deleteAll_withNonExistentId_throwNotDeletedException(List<Long> ids){
        doThrow(EmptyResultDataAccessException.class).when(shoppingListItemRepo)
                .deleteById(anyLong());

        assertThrows(NotDeletedException.class,
                ()-> shoppingListItemService.deleteAllShoppingListItemsByListOfId(ids));
        verifyDeleteByIdMethodCall(1);
    }

    @Test
    public void getShoppingListByHabitId_withIncorrectId_expectEmptyList(){
        when(shoppingListItemRepo.getAllShoppingListItemIdByHabitIdISContained(anyLong()))
                .thenReturn(new ArrayList<>());

        var actual = shoppingListItemService.getShoppingListByHabitId(anyLong());

        assertTrue(actual.isEmpty());
        verifyGetAllShoppingListItemIdByHabitIdISContainedMethodCall();
    }

    @Test
    public void getShoppingListByHabitId_withCorrectId_expectSuccess(){
        var shoppingListItemList = singletonList(shoppingListItem);
        var shoppingListItemIds = singletonList(1L);
        var expected = singletonList(new ShoppingListItemManagementDto());

        when(shoppingListItemRepo.getAllShoppingListItemIdByHabitIdISContained(anyLong()))
                .thenReturn(shoppingListItemIds);
        when(shoppingListItemRepo.getShoppingListByListOfId(shoppingListItemIds))
                .thenReturn(shoppingListItemList);
       when(modelMapper.map(any(ShoppingListItem.class), eq(ShoppingListItemManagementDto.class)))
                .thenReturn(expected.get(0));

        var actual = shoppingListItemService.getShoppingListByHabitId(anyLong());

        assertEquals(expected,actual);
        verifyGetAllShoppingListItemIdByHabitIdISContainedMethodCall();
        verify(shoppingListItemRepo).getShoppingListByListOfId(shoppingListItemIds);
        verifyModelMapperMapMethodCall();
    }


    @Test
    public void findAllShoppingListItemsForManagementPageNotContained_withCorrectHabitId_expectSuccess(){
        assertServiceMethodHasCorrectFlow((service, page)-> service.findAllShoppingListItemsForManagementPageNotContained(1L,page),
                (repo, page) -> repo.getShoppingListByListOfIdPageable(anyList(),eq(page)));
    }


    private ShoppingListItemPostDto getShoppingListItemPostDto() {
        var post = new ShoppingListItemPostDto();
        post.setShoppingListItem(new ShoppingListItemRequestDto(1L));
        return post;
    }

    private Type getType() {
        return new TypeToken<List<LanguageTranslationDTO>>() {
        }.getType();
    }

    private static Stream<List<Long>> idsProvider(){
        return Stream.of(Arrays.asList(1L,2L,3L,4L,5L,6L));
    }

    private void verifyDeleteByIdMethodCall(int amount) {
        verify(shoppingListItemRepo,times(amount)).deleteById(anyLong());
    }

    private void verifyFindAllByLanguageCodeMethodCall() {
        verify(shoppingListItemTranslationRepo).findAllByLanguageCode(anyString());
    }

    private void verifyModelMapperMapMethodCall() {
        verify(modelMapper).map(any(),any());
    }

    private void verifyModelMapperMapWithTypeMethodCall(Type type) {
        verify(modelMapper).map(any(),eq(type));
    }

    private void verifyGetAllShoppingListItemIdByHabitIdISContainedMethodCall() {
        verify(shoppingListItemRepo).getAllShoppingListItemIdByHabitIdISContained(anyLong());
    }
}
