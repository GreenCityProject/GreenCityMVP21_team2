package greencity.mapping;

import greencity.dto.language.LanguageVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.Language;
import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends AbstractConverter<UserVO, User> {
    @Override
    protected User convert(UserVO userVO) {
        return User.builder()
                .id(userVO.getId())
                .name(userVO.getName())
                .email(userVO.getEmail())
                .role(userVO.getRole())
                .userCredo(userVO.getUserCredo())
                .firstName(userVO.getFirstName())
                .emailNotification(userVO.getEmailNotification())
                .userStatus(userVO.getUserStatus())
                .rating(userVO.getRating())
                .verifyEmail((userVO.getVerifyEmail() != null) ? VerifyEmail.builder()
                        .id(userVO.getVerifyEmail().getId())
                        .user(User.builder()
                                .id(userVO.getVerifyEmail().getUser().getId())
                                .name(userVO.getVerifyEmail().getUser().getName())
                                .build())
                        .expiryDate(userVO.getVerifyEmail().getExpiryDate())
                        .token(userVO.getVerifyEmail().getToken())
                        .build() : null)
                .refreshTokenKey(userVO.getRefreshTokenKey())
                .ownSecurity(userVO.getOwnSecurity() != null ? OwnSecurity.builder()
                        .id(userVO.getOwnSecurity().getId())
                        .password(userVO.getOwnSecurity().getPassword())
                        .user(User.builder()
                                .id(userVO.getOwnSecurity().getUser().getId())
                                .email(userVO.getOwnSecurity().getUser().getEmail())
                                .role(userVO.getOwnSecurity().getUser().getRole())
                                .build())
                        .build() : null)
                .dateOfRegistration(userVO.getDateOfRegistration())
                .profilePicturePath(userVO.getProfilePicturePath())
                .city(userVO.getCity())
                .showShoppingList(userVO.getShowShoppingList())
                .showEcoPlace(userVO.getShowEcoPlace())
                .showLocation(userVO.getShowLocation())
                .language(//userVO.getLanguageVO() != null ?
                        Language.builder()
                                .id(userVO.getLanguageVO().getId())
                                .code(userVO.getLanguageVO().getCode())
                                .build()
                        // : null
                )
                .build();

    }
}
