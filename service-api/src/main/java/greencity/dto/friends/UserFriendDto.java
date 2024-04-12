package greencity.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@SuppressWarnings("java:S107")
public class UserFriendDto {
    private Long id;
    private String name;
    private String email;
    private Double rating;
    private Long mutualFriends;
    private String profilePicturePath;
    private Long chatId;
    private String friendStatus;
    private Long requesterId;

    /**
     * Constructor for basic user information.
     */
    public UserFriendDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
