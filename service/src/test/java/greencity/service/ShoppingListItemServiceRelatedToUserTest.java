package greencity.service;

import static greencity.ModelUtils.*;
import static java.util.Collections.*;
import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import greencity.dto.shoppinglistitem.*;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.entity.HabitAssign;
import greencity.entity.UserShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import greencity.exception.exceptions.*;
import greencity.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemServiceRelatedToUserTest {
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

    private HabitAssign habitAssign;
    private List<ShoppingListItemRequestDto> shoppingListRequestItems;
    private List<Long> shoppingListItemIds;
    private UserShoppingListItem userShoppingListItem;

    @BeforeEach
    void setUp() {
        habitAssign = getHabitAssign();
        shoppingListRequestItems = getShoppingListRequestDtoItems();
        shoppingListItemIds = getShoppingListItemIds(shoppingListRequestItems);
        userShoppingListItem = new UserShoppingListItem();
        userShoppingListItem.setId(1L);
    }

    @ParameterizedTest
    @CsvSource({"1,1"})
    public void saveUserShoppingListItems_withIncorrectHabitOrUserId_throwUserHasNoShoppingListItems(long habitId, long userId){
        when(habitAssignRepo.findByHabitIdAndUserId(habitId,userId)).thenReturn(empty());
        assertThrows(UserHasNoShoppingListItemsException.class,
                ()->shoppingListItemService.saveUserShoppingListItems(userId,habitId,emptyList(),"en"));
    }

    @ParameterizedTest
    @CsvSource({"1,1,en"})
    public void saveUserShoppingListItems_withCorrectParameters_expectSuccess(long habitId,long userId, String langCode){
        when(habitAssignRepo.findByHabitIdAndUserId(habitId,userId)).thenReturn(of(habitAssign));
        when(userShoppingListItemRepo.getShoppingListItemsIdForHabit(habitAssign.getHabit().getId()))
                .thenReturn(shoppingListItemIds);
        when(userShoppingListItemRepo.getAllAssignedShoppingListItems(habitAssign.getId()))
                .thenReturn(singletonList(132L));
        when(modelMapper.map(shoppingListRequestItems.get(0),UserShoppingListItem.class))
                .thenReturn(userShoppingListItem);
        when(userShoppingListItemRepo.saveAll(any())).thenReturn(emptyList());

        shoppingListItemService.saveUserShoppingListItems(userId,habitId,shoppingListRequestItems,langCode);

        assertEquals(habitAssign,userShoppingListItem.getHabitAssign());
        assertTrue(habitAssign.getUserShoppingListItems().contains(userShoppingListItem));

        verifySaveAllMethodCall();
        verifyGetAllAssignedShoppingListItemsMethodCall();
        verifyGetShoppingListItemsIdForHabitMethodCall();
        verifyModelMapperMapMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,en"})
    public void saveUserShoppingListItems_withNonAssignedToHabitItem_throwNotFound(long habitId,long userId, String langCode){
        when(habitAssignRepo.findByHabitIdAndUserId(habitId,userId)).thenReturn(of(habitAssign));
        when(userShoppingListItemRepo.getShoppingListItemsIdForHabit(habitAssign.getHabit().getId()))
                .thenReturn(emptyList());

        assertThrows(NotFoundException.class,
                ()-> shoppingListItemService.saveUserShoppingListItems(userId,habitId,shoppingListRequestItems,langCode));
        verifyFindByHabitIdAndUserIdMethodCall();
        verifyGetShoppingListItemsIdForHabitMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,en"})
    public void saveUserShoppingListItems_withAssignedToUserItem_throwWrongIdException(long habitId,long userId, String langCode){
        when(habitAssignRepo.findByHabitIdAndUserId(habitId,userId)).thenReturn(of(habitAssign));
        when(userShoppingListItemRepo.getShoppingListItemsIdForHabit(habitAssign.getHabit().getId()))
                .thenReturn(shoppingListItemIds);
        when(userShoppingListItemRepo.getAllAssignedShoppingListItems(habitAssign.getId()))
                .thenReturn(shoppingListItemIds);

        assertThrows(WrongIdException.class,
                ()-> shoppingListItemService.saveUserShoppingListItems(userId,habitId,shoppingListRequestItems,langCode));

        verifyFindByHabitIdAndUserIdMethodCall();
        verifyGetShoppingListItemsIdForHabitMethodCall();
        verifyGetAllAssignedShoppingListItemsMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1"})
    public void getUserShoppingList_withNullItemsAndIncorrectHabitOrUserId_expectEmptyList(long habitId, long userId){
        when(habitAssignRepo.findByHabitIdAndUserId(habitId,userId)).thenReturn(empty());
        var actual = shoppingListItemService.saveUserShoppingListItems(userId,habitId,null,"en");

        assertTrue(actual.isEmpty());
        verifyFindByHabitIdAndUserIdMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,en"})
    public void getUserShoppingList_withNullItemsAndCorrectIds_expectSuccess(long habitId, long userId,String langCode){
        var userItems = getUserItems();
        var userItemsResponse = getUserShoppingListItemResponseDto();
        var translation = getShoppingListItemTranslation();

        when(habitAssignRepo.findByHabitIdAndUserId(habitId,userId)).thenReturn(of(habitAssign));
        when(userShoppingListItemRepo.findAllByHabitAssingId(habitId)).thenReturn(userItems);
        when(modelMapper.map(userItems.get(0), UserShoppingListItemResponseDto.class))
                .thenReturn(userItemsResponse);
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(langCode,userItemsResponse.getId()))
                .thenReturn(translation);

        var actual = shoppingListItemService.saveUserShoppingListItems(userId,habitId,null,langCode);

        assertFalse(actual.isEmpty());
        assertEquals(translation.getContent(),actual.get(0).getText());
        verifyFindByHabitIdAndUserIdMethodCall();
        verifyModelMapperMapMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,en"})
    public void getUserShoppingListByHabitAssignId_withWrongHabitId_expectNotFound(
            long habitAssignId,long userId, String langCode){
        when(habitAssignRepo.findById(habitAssignId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                ()->shoppingListItemService.getUserShoppingListByHabitAssignId(habitAssignId,userId,langCode));
        verifyHabitRepoFindByIdMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,en"})
    public void getUserShoppingListByHabitAssignId_withNotEqualsIds_UserHasNoPermissionToAccess(
            long habitAssignId,long userId, String langCode){
        habitAssign.getUser().setId(2L);

        when(habitAssignRepo.findById(habitAssignId)).thenReturn(of(habitAssign));

        assertThrows(UserHasNoPermissionToAccessException.class,
                ()->shoppingListItemService.getUserShoppingListByHabitAssignId(habitAssignId,userId,langCode));
        verifyHabitRepoFindByIdMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,en"})
    public void getUserShoppingListByHabitAssignId_withCorrectData_expectSuccess(
            long habitAssignId,long userId, String langCode){
        habitAssign.getUser().setId(userId);
        var userItems = getUserItems();
        var translation = getShoppingListItemTranslation();
        var responseItemDto = getUserShoppingListItemResponseDto();

        when(habitAssignRepo.findById(habitAssignId)).thenReturn(of(habitAssign));
        when(userShoppingListItemRepo.findAllByHabitAssingId(habitAssignId))
                .thenReturn(userItems);
        when(modelMapper.map(any(UserShoppingListItem.class),eq(UserShoppingListItemResponseDto.class)))
                .thenReturn(responseItemDto);
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(langCode, responseItemDto.getId()))
                .thenReturn(translation);

        var actual = shoppingListItemService.getUserShoppingListByHabitAssignId(habitAssignId,userId,langCode);

        assertEquals(translation.getContent(),responseItemDto.getText());
        assertEquals(userItems.size(),actual.size());
        verifyHabitRepoFindByIdMethodCall();
        verifyFindByLangAndUserShoppingListItemIdMethodCall();
        verifyModelMapperMapMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,en"})
    public void getUserShoppingListItemsByHabitAssignIdAndStatusInProgress_withCorrectData_expectSuccess(
            Long habitAssignId, String langCode) {
        var userItems = getUserItems();
        var responseItemDto = getUserShoppingListItemResponseDto();
        var translation = getShoppingListItemTranslation();

        when(userShoppingListItemRepo.findUserShoppingListItemsByHabitAssignIdAndStatusInProgress(habitAssignId))
                .thenReturn(userItems);
        when(modelMapper.map(any(UserShoppingListItem.class), eq(UserShoppingListItemResponseDto.class)))
                .thenReturn(responseItemDto);
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(langCode, responseItemDto.getId()))
                .thenReturn(translation);

        var actual = shoppingListItemService.getUserShoppingListItemsByHabitAssignIdAndStatusInProgress(habitAssignId,langCode);

        assertEquals(translation.getContent(),responseItemDto.getText());
        assertEquals(userItems.size(),actual.size());
        verify(userShoppingListItemRepo).findUserShoppingListItemsByHabitAssignIdAndStatusInProgress(habitAssignId);
        verifyFindByLangAndUserShoppingListItemIdMethodCall();
        verifyModelMapperMapMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,1"})
    public void deleteUserItemByItemIdAndUserIdAndHabitId_withCorrectParameters_expectSuccess(
            long item,long habitId,long userId){
        when(habitAssignRepo.findByHabitIdAndUserId(habitId,userId)).thenReturn(of(new HabitAssign()));

        shoppingListItemService.deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(item,userId,habitId);

        verifyFindByHabitIdAndUserIdMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,en"})
    public void updateUserShoppingListItemStatus_withNonActiveItem_throwUserShoppingListItemStatusNotUpdated(
            Long userId, Long itemId, String language){
        userShoppingListItem.setStatus(ShoppingListItemStatus.DISABLED);
        when(userShoppingListItemRepo.getOne(itemId)).thenReturn(userShoppingListItem);

        assertThrows(UserShoppingListItemStatusNotUpdatedException.class,
                ()->shoppingListItemService.updateUserShopingListItemStatus(userId, itemId, language));
        verify(userShoppingListItemRepo).getOne(itemId);
    }

    @ParameterizedTest
    @CsvSource({"1,1,en"})
    public void updateUserShoppingListItemStatus_withActiveItem_expectSuccess(
            Long userId, Long itemId, String language){
        var itemResponseDto = getUserShoppingListItemResponseDto();
        var translation = getShoppingListItemTranslation();

        userShoppingListItem.setStatus(ShoppingListItemStatus.ACTIVE);
        when(userShoppingListItemRepo.getOne(itemId)).thenReturn(userShoppingListItem);
        when(modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class))
                .thenReturn(itemResponseDto);
        when(userShoppingListItemRepo.save(userShoppingListItem)).thenReturn(userShoppingListItem);
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(language, itemId))
                .thenReturn(translation);

        shoppingListItemService.updateUserShopingListItemStatus(userId, itemId, language);

        assertEquals(translation.getContent(), itemResponseDto.getText());
        verify(userShoppingListItemRepo).getOne(itemId);
        verifyModelMapperMapMethodCall();
        verify(userShoppingListItemRepo).save(userShoppingListItem);
        verifyFindByLangAndUserShoppingListItemIdMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,en,ACTIVE"})
    public void updateUserShoppingListItemStatus_withIncorrectItemId_expectNotFound(
            Long userId,Long userShoppingListItemId, String language, String status){
        when(userShoppingListItemRepo.getAllByUserShoppingListIdAndUserId(userShoppingListItemId,userId))
                .thenReturn(new ArrayList<>());

        assertThrows(NotFoundException.class,
                () -> shoppingListItemService.updateUserShoppingListItemStatus(userId, userShoppingListItemId, language, status));
        verifyGetAllByUserShoppingListIdAndUserIdMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,en,BAD_STATUS"})
    public void updateUserShoppingListItemStatus_withIncorrectStatus_expectBadRequest(
            Long userId,Long userShoppingListItemId, String language, String status){
        var userShoppingListItems = singletonList(userShoppingListItem);
        when(userShoppingListItemRepo.getAllByUserShoppingListIdAndUserId(userShoppingListItemId,userId))
                .thenReturn(userShoppingListItems);

        assertThrows(BadRequestException.class,
                () -> shoppingListItemService.updateUserShoppingListItemStatus(userId, userShoppingListItemId, language, status));
        verifyGetAllByUserShoppingListIdAndUserIdMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"1,1,en,ACTIVE"})
    public void updateUserShoppingListItemStatus_withCorrectParameters_expectSuccess(
            Long userId,Long userShoppingListItemId, String language, String status){
        var userShoppingListItems = singletonList(userShoppingListItem);
        var itemResponseDto = getUserShoppingListItemResponseDto();
        var translation = getShoppingListItemTranslation();

        when(userShoppingListItemRepo.getAllByUserShoppingListIdAndUserId(userShoppingListItemId,userId))
                .thenReturn(userShoppingListItems);
        when(userShoppingListItemRepo.saveAll(userShoppingListItems)).thenReturn(userShoppingListItems);
        when(modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class))
                .thenReturn(itemResponseDto);
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(language, itemResponseDto.getId()))
                .thenReturn(translation);

        var actual = shoppingListItemService.updateUserShoppingListItemStatus(userId, userShoppingListItemId, language, status);

        assertEquals(itemResponseDto.getStatus(),actual.get(0).getStatus());
        assertEquals(translation.getContent(),actual.get(0).getText());
        verifyGetAllByUserShoppingListIdAndUserIdMethodCall();
        verifySaveAllMethodCall();
        verifyModelMapperMapMethodCall();
        verifyFindByLangAndUserShoppingListItemIdMethodCall();
    }


    @ParameterizedTest
    @CsvSource({"1,1,1"})
    public void deleteUserItemByItemIdAndUserIdAndHabitId_withIncorrectUserOrHabitId_throwNotFoundException(
            long item,long habitId,long userId){
        when(habitAssignRepo.findByHabitIdAndUserId(habitId,userId)).thenReturn(empty());

        assertThrows(NotFoundException.class,
                ()->shoppingListItemService.deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(item,userId,habitId));
        verifyFindByHabitIdAndUserIdMethodCall();
    }

    @ParameterizedTest
    @CsvSource({"'1,2,3,4,5'"})
    public void deleteUserShoppingListItems_withCorrectIds_expectSuccess(String ids){
        var userItem = getUserShoppingListItem();
        var expectedIds = convertToLongIds(ids);
        when(userShoppingListItemRepo.findById(anyLong())).thenReturn(of(userItem));
        doNothing().when(userShoppingListItemRepo).delete(userItem);

        var actualIds = shoppingListItemService.deleteUserShoppingListItems(ids);

        assertEquals(expectedIds,actualIds);
        verifyItemRepoFindByIdMethodCall(expectedIds.size());
        verify(userShoppingListItemRepo,times(expectedIds.size())).delete(userItem);
    }

    private List<Long> convertToLongIds(String ids){
        return Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .toList();
    }


    @ParameterizedTest
    @CsvSource({"1"})
    public void deleteUserShoppingListItems_withNonExistentId_expectSuccess(String ids){
        when(userShoppingListItemRepo.findById(anyLong())).thenReturn(empty());

        assertThrows(NotFoundException.class,
                ()-> shoppingListItemService.deleteUserShoppingListItems(ids));
        verifyItemRepoFindByIdMethodCall(1);
    }

    @ParameterizedTest
    @CsvSource({"incorrect ids"})
    public void deleteUserShoppingListItems_withIncorrectIds_throwNumberFormatException(String ids){
        assertThrows(NumberFormatException.class,
                ()-> shoppingListItemService.deleteUserShoppingListItems(ids));
    }

    @ParameterizedTest
    @CsvSource({"1,en"})
    public void findInProgressByUserIdAndLanguageCode_withCorrectIdAndCode(long userId, String langCode){
        var translation = getShoppingListItemTranslation();
        var translationList = singletonList(getShoppingListItemTranslation());
        var shoppingListItemDto = buildShoppingListItemDto(translation);

        when(shoppingListItemRepo.findInProgressByUserIdAndLanguageCode(userId, langCode))
                .thenReturn(translationList);
        when(modelMapper.map(translation, ShoppingListItemDto.class)).thenReturn(shoppingListItemDto);

        var actual = shoppingListItemService.findInProgressByUserIdAndLanguageCode(userId,langCode);

        assertEquals(singletonList(shoppingListItemDto),actual);
        verify(shoppingListItemRepo).findInProgressByUserIdAndLanguageCode(userId, langCode);
    }

    private ShoppingListItemDto buildShoppingListItemDto(ShoppingListItemTranslation translation) {
        return ShoppingListItemDto.builder()
                .id(translation.getShoppingListItem().getId())
                .text(translation.getContent())
                .status(ShoppingListItemStatus.INPROGRESS.toString())
                .build();
    }

    private List<ShoppingListItemRequestDto> getShoppingListRequestDtoItems() {
        return singletonList(new ShoppingListItemRequestDto(1L));
    }

    private List<Long> getShoppingListItemIds(List<ShoppingListItemRequestDto> shoppingLisItems) {
        return shoppingLisItems.stream()
                .map(ShoppingListItemRequestDto::getId)
                .collect(Collectors.toList());
    }

    private ShoppingListItemTranslation getShoppingListItemTranslation() {
        return getShoppingListItemTranslations().get(0);
    }

    private List<UserShoppingListItem> getUserItems() {
        return singletonList(new UserShoppingListItem());
    }

    private void verifySaveAllMethodCall() {
        verify(userShoppingListItemRepo).saveAll(any());
    }

    private  UserShoppingListItemResponseDto getUserShoppingListItemResponseDto() {
        var responseItemDto = new UserShoppingListItemResponseDto();
        responseItemDto.setId(1L);
        return responseItemDto;
    }

    private void verifyItemRepoFindByIdMethodCall(int amount) {
        verify(userShoppingListItemRepo,times(amount)).findById(anyLong());
    }

    private void verifyHabitRepoFindByIdMethodCall() {
        verify(habitAssignRepo).findById(anyLong());
    }

    private void verifyFindByLangAndUserShoppingListItemIdMethodCall() {
        verify(shoppingListItemTranslationRepo).findByLangAndUserShoppingListItemId(anyString(), anyLong());
    }

    private void verifyGetAllAssignedShoppingListItemsMethodCall() {
        verify(userShoppingListItemRepo).getAllAssignedShoppingListItems(anyLong());
    }

    private void verifyGetShoppingListItemsIdForHabitMethodCall() {
        verify(userShoppingListItemRepo).getShoppingListItemsIdForHabit(anyLong());
    }

    private void verifyFindByHabitIdAndUserIdMethodCall() {
        verify(habitAssignRepo).findByHabitIdAndUserId(anyLong(),anyLong());
    }

    private void verifyModelMapperMapMethodCall() {
        verify(modelMapper).map(any(),any());
    }

    private void verifyGetAllByUserShoppingListIdAndUserIdMethodCall() {
        verify(userShoppingListItemRepo).getAllByUserShoppingListIdAndUserId(anyLong(), anyLong());
    }
}