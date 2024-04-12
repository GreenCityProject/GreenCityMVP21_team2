package greencity.service;

import greencity.dto.user.UserVO;

import java.util.List;

public interface TaggedUsersService {
    List<UserVO> findTaggedUsersFromText(String text);
}
