package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.DTO.ReviewRequest;
import ru.yandex.practicum.filmorate.DTO.ReviewResponse;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponse createReview(@RequestBody ReviewRequest request) {
        Review created = reviewService.createReview(request);
        return ReviewMapper.toResponse(created);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponse updateReview(@RequestBody ReviewRequest request) {
        Review updated = reviewService.updateReview(request);
        return ReviewMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Было HttpStatus.OK
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponse getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        return ReviewMapper.toResponse(review);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getReviews(@RequestParam(required = false) Integer filmId,
                                           @RequestParam(defaultValue = "10") int count) {
        List<Review> reviews = reviewService.getReviews(filmId, count);
        return ReviewMapper.toResponseList(reviews);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewResponse addLike(@PathVariable Long id,
                                  @PathVariable int userId) {
        return ReviewMapper.toResponse(reviewService.addLike(id, userId));
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewResponse addDislike(@PathVariable Long id,
                                     @PathVariable int userId) {
        return ReviewMapper.toResponse(reviewService.addDislike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewResponse removeLike(@PathVariable Long id,
                                     @PathVariable int userId) {
        return ReviewMapper.toResponse(reviewService.removeLike(id, userId));
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewResponse removeDislike(@PathVariable Long id,
                                        @PathVariable int userId) {
        return ReviewMapper.toResponse(reviewService.removeDislike(id, userId));
    }
}