package greencity.mapping;

import greencity.dto.events.EventAuthorDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventAuthorDtoMapper extends AbstractConverter<User, EventAuthorDto> {
    @Override
    protected EventAuthorDto convert(User user) {
        return EventAuthorDto.builder()
                .id(user.getId())
                .name(user.getName())
                .organizerRating(user.getRating())
                .build();
    }
}
