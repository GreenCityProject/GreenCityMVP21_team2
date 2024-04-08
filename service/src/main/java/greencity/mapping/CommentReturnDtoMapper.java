package greencity.mapping;

import greencity.dto.comment.CommentReturnDto;
import greencity.dto.rate.EstimateAddDto;
import greencity.entity.Comment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class CommentReturnDtoMapper extends AbstractConverter<Comment, CommentReturnDto> {
    @Override
    protected CommentReturnDto convert(Comment comment) {
        return CommentReturnDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdDate(comment.getCreatedDate())
                .estimate(EstimateAddDto.builder()
                        .rate(comment.getEstimate())
                        .build())
                .build();
    }
}
