package greencity.dto.events;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EventDtoToUpdate {

    private Long id;

    private EventAuthorDto organizer;

    private String title;

    private String titleImage;

    private String description;

    private String creationDate;

    private Boolean isFavorite;

    private Boolean isSubscribed;

    private Boolean open;

    private List<String> tags;

    private List<EventDateLocationDto> datesLocations;

    private List<String> additionalImages;

    private List<String> imagesToDelete;
}
