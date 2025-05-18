package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long reviewId);

    Review getReviewById(Long reviewId);

    List<Review> getReviews(Integer filmId, int count);

    void addLike(Long reviewId, int userId);

    void addDislike(Long reviewId, int userId);

    void removeLike(Long reviewId, int userId);

    void removeDislike(Long reviewId, int userId);
}