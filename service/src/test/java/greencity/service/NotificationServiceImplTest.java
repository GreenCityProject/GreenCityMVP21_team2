package greencity.service;

import greencity.dto.CreatNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationStatus;
import greencity.enums.NotificationType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.NotificationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static greencity.ModelUtils.*;
import static greencity.constant.AppConstant.*;
import static greencity.enums.NotificationStatus.READ;
import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepo notificationRepo;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void getAllNotification_WithCorrectReceiverIdParameter_expectSuccess(){
        var receiverId = 1L;
        var notification = getNotification();
        var notificationPage = new PageImpl<>(singletonList(notification));
        var notificationDtos = (singletonList(getNotificationDto()));
        var pageable = Pageable.unpaged();

        when(notificationRepo.findAllByReceiverId(receiverId,pageable))
                .thenReturn(notificationPage);
        when(modelMapper.map(notification, NotificationDto.class))
                .thenReturn(notificationDtos.get(0));

        var actual = notificationService.getAllNotifications(receiverId);
        assertEquals(notificationDtos,actual);
    }

    @Test
    void getLatestUnreadNotification_WithCorrectReceiverIdParameter_expectSuccess(){
        var receiverId = 1L;
        var notification = getNotification();
        var notificationPage = new PageImpl<>(singletonList(notification));
        var notificationDtos = (singletonList(getNotificationDto()));
        var pageable = getLatestPageable();

        when(notificationRepo.findAllUnreadByReceiverId(receiverId,pageable))
                .thenReturn(notificationPage);

        when(modelMapper.map(notification, NotificationDto.class))
                .thenReturn(notificationDtos.get(0));

        var actual = notificationService.getLatestUnreadNotifications(receiverId);
        assertEquals(notificationDtos,actual);
    }

    private PageRequest getLatestPageable() {
        return PageRequest.ofSize(LATEST_NOTIFICATION_SIZE)
                .withSort(Sort.by(LATEST_NOTIFICATION_SORT_FIELD).descending());
    }

    @Test
    void removeNotificationById_withCorrectId_doesNotThrowException(){
        var id = 1L;
        var notification = getNotification();
        var userVO = getUserVO();

        mockFindByIdWithUserReturnNotification(id,notification);
        doNothing().when(notificationRepo).delete(notification);

        assertDoesNotThrow(()->notificationService.removeNotificationById(userVO,id));
        verifyFindByIdFetchUserMethodCall();
    }

    @Test
    void removeNotificationById_withIncorrectId_throwsNotFound(){
        var id = 1L;
        var userVO = getUserVO();

        mockFindByIdRepoMethodWithEmptyResult(id);
        assertThrows(NotFoundException.class,
                ()->notificationService.removeNotificationById(userVO,id));
        verifyFindByIdFetchUserMethodCall();
    }

    @Test
    void removeNotificationById_withNotCurrentUser_throwsUserHasNoPermissionToAccess(){
        var id = 1L;
        var notification = getNotification();
        var userVO = getUserVO();
        userVO.setId(notification.getReceiver().getId()+1);

        mockFindByIdWithUserReturnNotification(id,notification);

        assertThrows(UserHasNoPermissionToAccessException.class,
                ()->notificationService.removeNotificationById(userVO,id));
        verifyFindByIdFetchUserMethodCall();
    }

    @Test
    void changeStatusToRead_withCorrectId_doesNotThrowException(){
        var id = 1L;
        var notification = getNotification();
        var notificationDto = getNotificationDto();
        var userVO = getUserVO();

        mockFindByIdWithUserReturnNotification(id,notification);
        when(modelMapper.map(notification,NotificationDto.class)).thenReturn(notificationDto);

        assertEquals(notificationDto,notificationService.changeStatusToRead(id,userVO));
        assertEquals(READ, notification.getStatus());
        assertEquals(now().plusDays(READ_NOTIFIC_ACTIVE_DAYS_TIME).getDayOfMonth(),
                notification.getExpireAt().getDayOfMonth());

        verifyFindByIdFetchUserMethodCall();
        verifyMapMethodCall(1);
    }


    @Test
    void changeStatusToRead_withIncorrectId_throwsNotFound(){
        var id = 1L;
        var userVO = getUserVO();

        mockFindByIdRepoMethodWithEmptyResult(id);
        assertThrows(NotFoundException.class,
                ()->notificationService.changeStatusToRead(id,userVO));
        verifyFindByIdFetchUserMethodCall();
    }

    @Test
    void changeStatusToRead_withNotCurrentUser_throwsUserHasNoPermissionToAccess(){
        var id = 1L;
        var notification = getNotification();
        var userVO = getUserVO();
        userVO.setId(notification.getReceiver().getId()+1);

        mockFindByIdWithUserReturnNotification(id,notification);

        assertThrows(UserHasNoPermissionToAccessException.class,
                ()->notificationService.changeStatusToRead(id,userVO));
        verifyFindByIdFetchUserMethodCall();
    }


    @Test
    void changeStatusToUnread_withCorrectId_doesNotThrowException(){
        var id = 1L;
        var notification = getNotification();
        notification.setStatus(READ);
        var notificationDto = getNotificationDto();
        var userVO = getUserVO();

        mockFindByIdWithUserReturnNotification(id,notification);
        when(modelMapper.map(notification,NotificationDto.class)).thenReturn(notificationDto);

        assertEquals(notificationDto,notificationService.changeStatusToUnread(id,userVO));
        assertEquals(NotificationStatus.UNREAD, notification.getStatus());

        verifyFindByIdFetchUserMethodCall();
        verifyMapMethodCall(1);
    }


    @Test
    void changeStatusToUnread_withIncorrectId_throwsNotFound(){
        var id = 1L;
        var userVO = getUserVO();

        mockFindByIdRepoMethodWithEmptyResult(id);
        assertThrows(NotFoundException.class,
                ()->notificationService.changeStatusToUnread(id,userVO));
        verifyFindByIdFetchUserMethodCall();
    }

    @Test
    void changeStatusToUnread_withNotCurrentUser_throwsUserHasNoPermissionToAccess(){
        var id = 1L;
        var notification = getNotification();
        var userVO = getUserVO();
        userVO.setId(notification.getReceiver().getId()+1);

        mockFindByIdWithUserReturnNotification(id,notification);

        assertThrows(UserHasNoPermissionToAccessException.class,
                ()->notificationService.changeStatusToUnread(id,userVO));
        verifyFindByIdFetchUserMethodCall();
    }

    @Test
    void createNotification_withCorrectData_expectDoesNotThrow(){
        var userVO = getUserVO();
        var user = getUser();
        var notification = getNotification();
        var notificationDto = buildCreateNotificationDto(userVO);

        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(notificationRepo.save(any(Notification.class))).thenReturn(notification);

        assertDoesNotThrow(()->notificationService.createNotification(notificationDto));
        verifyMapMethodCall(2);
    }

    private CreatNotificationDto buildCreateNotificationDto(UserVO userVO) {
        return CreatNotificationDto.builder()
                .notificationType(NotificationType.LIKED_EVENT)
                .evaluator(userVO)
                .receiver(userVO)
                .relatedEntityId(1L)
                .build();
    }


    private void mockFindByIdRepoMethodWithEmptyResult(long id) {
        when(notificationRepo.findByIdFetchUser(id))
                .thenReturn(Optional.empty());
    }

    private void mockFindByIdWithUserReturnNotification(long id, Notification notification) {
        when(notificationRepo.findByIdFetchUser(id))
                .thenReturn(Optional.of(notification));
    }


    private void verifyFindByIdFetchUserMethodCall(){
        verify(notificationRepo).findByIdFetchUser(anyLong());
    }

    private void verifyMapMethodCall(int times) {
        verify(modelMapper,times(times)).map(any(),any());
    }
}
