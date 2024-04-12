package greencity.service;

import greencity.dto.newsSubscriber.NewsSubscriberRequestDto;
import greencity.dto.newsSubscriber.NewsSubscriberResponseDto;
import greencity.entity.NewsSubscriber;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NewsSubscriberRepo;
import greencity.security.jwt.JwtTool;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NewsSubscriberServiceImpl implements NewsSubscriberService{
    private final NewsSubscriberRepo newsSubscriberRepo;
    private final JwtTool jwtTool;

    @Override
    public NewsSubscriberRequestDto saveNewsSubscriber(NewsSubscriberRequestDto newsSubscriberRequestDto) {
        if (isEmailAlreadySubscribed(newsSubscriberRequestDto.getEmail()))
            throw new BadRequestException("Email subscribed already");
        this.newsSubscriberRepo.save(new NewsSubscriber(null, newsSubscriberRequestDto.getEmail(), jwtTool.generateTokenKey()));
        return newsSubscriberRequestDto;
    }

    @Override
    @Transactional
    public Long unsubscribe(String email, String unsubscribeToken) {
        if (!isEmailAlreadySubscribed(email))
            throw new NotFoundException("Subscriber's email doesn't exist");
        if (isUnsubscribeCanBePerformed(email, unsubscribeToken)) {
            newsSubscriberRepo.deleteByEmail(email);
            return 1L;
        }
        throw new BadRequestException("Wrong parameters");
    }

    private boolean isUnsubscribeCanBePerformed(String email, String unsubscribeToken) {
        Optional<NewsSubscriber> newsSubscriberOptional = newsSubscriberRepo.findByEmail(email);
        if (newsSubscriberOptional.isPresent()) {
            NewsSubscriber newsSubscriber = newsSubscriberOptional.get();
            return newsSubscriber.getUnsubscribeToken().equals(unsubscribeToken);
        } else return false;
    }

    private boolean isEmailAlreadySubscribed(String email){
        return this.newsSubscriberRepo.findByEmail(email).isPresent();
    }

    @Override
    public List<NewsSubscriberResponseDto> getAll() {
        List<NewsSubscriber> subscribers = this.newsSubscriberRepo.findAll();
        return subscribers.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private NewsSubscriberResponseDto mapToDto(NewsSubscriber subscriber) {
        return NewsSubscriberResponseDto.builder()
                .email(subscriber.getEmail())
                .unsubscribeToken(subscriber.getUnsubscribeToken())
                .build();
    }
}
