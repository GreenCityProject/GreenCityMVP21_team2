package greencity.dto.events;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class EventViewDto {

    private String eventTime= "";
    private String location = "";
    private String type = "";
    private String status = "";

    public boolean isEmpty() {
        return eventTime.isEmpty() && location.isEmpty()
                && status.isEmpty() && type.isEmpty();
    }
}
