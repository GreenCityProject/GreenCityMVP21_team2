package greencity.service;

import greencity.constant.AppConstant;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaggedUsersServiceImpl implements TaggedUsersService {
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<UserVO> findTaggedUsersFromText(String text) {
        var names = extractNamesFormText(text);
        return getUsersByNames(names);
    }


    private static Matcher getMatcher(String text) {
        return Pattern.compile(AppConstant.TAGGING_REGEX).matcher(text);
    }

    private List<String> extractNamesFormText(String text) {
        var matcher = getMatcher(text);
        return findNames(matcher);
    }

    private List<String> findNames(Matcher matcher) {
        var names = new ArrayList<String>();
        while (matcher.find()) {
            names.add(matcher.group().substring(1));
        }
        return names;
    }

    private List<UserVO> getUsersByNames(List<String> names) {
        return names.stream()
                .map(this::findUserByName)
                .filter(Objects::nonNull)
                .map(this::mapToUserVO)
                .collect(Collectors.toList());
    }

    private User findUserByName(String name) {
        return userRepo.findByName(name).orElse(null);
    }

    private UserVO mapToUserVO(User user) {
        return modelMapper.map(user, UserVO.class);
    }
}
