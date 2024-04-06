package greencity.mapping;

import greencity.dto.comment.AddCommentDto;
import greencity.entity.Comment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper extends AbstractConverter<AddCommentDto, Comment> {
    @Override
    protected Comment convert(AddCommentDto addCommentDto) {
        return Comment.builder()
                .text(addCommentDto.getText())
                .estimate(addCommentDto.getEstimate().getRate())
                .build();
    }
}
