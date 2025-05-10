package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.DTO.ReviewRequest;
import ru.yandex.practicum.filmorate.DTO.ReviewResponse;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.stream.Collectors;

public class ReviewMapper {
    public static Review toReview(ReviewRequest request) {
        Review review = new Review();
        review.setReviewId(request.getReviewId());
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        return review;
    }

    public static ReviewResponse toResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setReviewId(review.getReviewId());
        response.setContent(review.getContent());
        response.setIsPositive(review.getIsPositive());
        response.setUserId(review.getUserId());
        response.setFilmId(review.getFilmId());
        response.setUseful(review.getUseful());
        return response;
    }

    public static List<ReviewResponse> toResponseList(List<Review> reviews) {
        return reviews.stream().map(ReviewMapper::toResponse).collect(Collectors.toList());
    }
}