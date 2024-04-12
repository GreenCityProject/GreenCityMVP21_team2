package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.newsSubscriber.NewsSubscriberRequestDto;
import greencity.dto.newsSubscriber.NewsSubscriberResponseDto;
import greencity.exception.exceptions.NotFoundException;
import greencity.service.NewsSubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/newsSubscriber")
@AllArgsConstructor
public class NewsSubscriberController {
    private final NewsSubscriberService newsSubscriberService;

    /**
     * Method for unsubscribing.
     *
     * @param email email of subscriber ({@link NewsSubscriberRequestDto}).
     * @param unsubscribeToken token that identify email owner.
     * @return {@link ResponseEntity}.
     */
    @Operation(summary = "Deleting an email form subscribe table")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = NotFoundException.class)))
    })
    @GetMapping("/unsubscribe")
    public ResponseEntity<Long> unsubscribe(@RequestParam @Email String email,
            @RequestParam String unsubscribeToken){
        return ResponseEntity.status(HttpStatus.OK).body(newsSubscriberService.unsubscribe(email, unsubscribeToken));
    }

    /**
     * Method for get all subscribers.
     *
     * @return list of {@link NewsSubscriberResponseDto} (subscriber's data).
     */
    @Operation(summary = "Get all emails for sending news")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping
    public ResponseEntity<List<NewsSubscriberResponseDto>> getAllSubscribers() {
        return ResponseEntity.status(HttpStatus.OK).body(newsSubscriberService.getAll());
    }

    /**
     * Method for save new news subscriber.
     *
     * @param newsSubscriberRequestDto to save subscriber's email
     * @return dto {@link NewsSubscriberRequestDto}
     */
    @Operation(summary = "Save email in database for receiving news")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping
    public ResponseEntity<NewsSubscriberRequestDto> saveNewsSubscriber(
            @Valid @RequestBody NewsSubscriberRequestDto newsSubscriberRequestDto){
        return ResponseEntity.status(HttpStatus.OK).body(
                this.newsSubscriberService.saveNewsSubscriber(newsSubscriberRequestDto));
    }
}
