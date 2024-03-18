package greencity.dto.events;

import greencity.dto.tag.TagUaEnDto;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "organizer")
@Builder
@EqualsAndHashCode
public class EventDto {

    private Long id;

    private EventAuthorDto organizer;

    private String title;

    private String titleImage;

    private String description;

    private String creationDate;

    private Boolean isFavorite;

    private Boolean isSubscribed;

    private Boolean open;

    private List<TagUaEnDto> tags;

    private List<EventDateLocationDto> dates;

    private List<String> additionalImages;
}
