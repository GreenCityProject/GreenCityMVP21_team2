package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.CommentRepo;
import greencity.repository.PlaceRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceCommentServiceImpl implements PlaceCommentService{
    private final CommentRepo commentRepo;
    private final PlaceRepo placeRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public CommentReturnDto save(AddCommentDto addCommentDto, Long placeId, UserVO user){
        Comment comment = modelMapper.map(addCommentDto, Comment.class);
        Place place = placeRepo.findById(placeId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.PLACE_NOT_FOUND_BY_ID + placeId));
        Byte grade = addCommentDto.getEstimate().getRate();
        setPlaceRating(grade, place, user);
        comment.setPlace(place);
        comment.setUser(modelMapper.map(user, User.class));
        commentRepo.save(comment);
        return modelMapper.map(comment, CommentReturnDto.class);
    }

    @Override
    public CommentReturnDto getCommentById(Long id) {
        return modelMapper.map(commentRepo.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + id)), CommentReturnDto.class);
    }

    @Override
    public PageableDto<CommentReturnDto> getAllComments (Pageable pageable){
        Page<Comment> commentPage = commentRepo.findAll(pageable);
        List<CommentReturnDto> commentReturnDtosList = commentPage.stream()
                .map(comment -> modelMapper.map(comment, CommentReturnDto.class))
                .collect(Collectors.toList());
        return new PageableDto<>(
                commentReturnDtosList,
                commentPage.getTotalElements(),
                commentPage.getPageable().getPageNumber(),
                commentPage.getTotalPages());
    }

    @Override
    public void delete(Long id, UserVO user){

        if (!user.getId().equals(commentRepo.findById(id).get().getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        commentRepo.deleteById(id);
    }

    private void setPlaceRating( Byte grade, Place place, UserVO userVO){
        Set<User> placeRatingUserVotes = place.getPlaceRatingUserVotes();
        User user = userRepo.findById(userVO.getId()).get();
        if (placeRatingUserVotes.contains(user)){
            throw new NotSavedException("You have already voted for this place.");
        } else {
            if (grade != null){
                double rating;

                if (place.getRating() == null){
                    rating = grade.doubleValue();
                } else {
                    double currentRating = place.getRating();
                    int currentUserVotes = placeRatingUserVotes.size();
                    rating = (((currentRating * currentUserVotes) + grade) / (currentUserVotes + 1));
                }
                placeRatingUserVotes.add(user);
                place.setRating(rating);
                place.setPlaceRatingUserVotes(placeRatingUserVotes);
                placeRepo.save(place);
            }
        }
    }
}
