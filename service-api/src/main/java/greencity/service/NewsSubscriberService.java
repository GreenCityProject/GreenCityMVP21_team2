package greencity.service;


import greencity.dto.newsSubscriber.NewsSubscriberRequestDto;
import greencity.dto.newsSubscriber.NewsSubscriberResponseDto;

import java.util.List;

public interface NewsSubscriberService {

    NewsSubscriberRequestDto saveNewsSubscriber(NewsSubscriberRequestDto newsSubscriberRequestDto);
    Long unsubscribe(String email, String unsubscribeToken);

    List<NewsSubscriberResponseDto> getAll();

}
