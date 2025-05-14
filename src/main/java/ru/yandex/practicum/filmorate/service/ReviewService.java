package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.DTO.ReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, UserService userService, FilmService filmService) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
    }

    @Transactional
    public Review createReview(ReviewRequest request) {
        if (request.getReviewId() != null) {
            throw new ValidationException("Для создания отзыва поле reviewId должно быть null");
        }

        if (request.getUserId() == null) {
            throw new ValidationException("User ID не может быть null");
        }
        if (request.getFilmId() == null) {
            throw new ValidationException("Film ID не может быть null");
        }

        if (!userService.userExists(request.getUserId())) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!filmService.filmExists(request.getFilmId())) {
            throw new NotFoundException("Фильм не найден");
        }

        if (request.getUserId() <= 0) {
            throw new ValidationException("User ID должен быть положительным");
        }
        if (request.getFilmId() <= 0) {
            throw new ValidationException("Film ID должен быть положительным");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new ValidationException("Содержание отзыва не может быть пустым");
        }
        Review review = new Review();
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        review.setUseful(0);

        return reviewStorage.createReview(review);
    }

    @Transactional
    public Review updateReview(ReviewRequest request) {
        if (request.getReviewId() == null) {
            throw new ValidationException("Для обновления отзыва поле reviewId должно быть установлено");
        }
        Review existing = reviewStorage.getReviewById(request.getReviewId());
        if (existing == null) {
            throw new NotFoundException("Отзыв не найден");
        }
        if (request.getContent() != null) {
            existing.setContent(request.getContent());
        }
        if (request.getIsPositive() != null) {
            existing.setIsPositive(request.getIsPositive());
        }
        return reviewStorage.updateReview(existing);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewStorage.getReviewById(reviewId);
        if (review != null) {
            reviewStorage.deleteReview(reviewId);
        }
    }

    public Review getReviewById(Long reviewId) {
        Review review = reviewStorage.getReviewById(reviewId);
        if (review == null) {
            throw new NotFoundException("Отзыв не найден");
        }
        return review;
    }

    public List<Review> getReviews(Integer filmId, int count) {
        return reviewStorage.getReviews(filmId, count);
    }

    @Transactional
    public Review addLike(Long reviewId, int userId) {
        checkReviewAndUser(reviewId, userId);
        reviewStorage.addLike(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    @Transactional
    public Review addDislike(Long reviewId, int userId) {
        checkReviewAndUser(reviewId, userId);
        reviewStorage.addDislike(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    @Transactional
    public Review removeLike(Long reviewId, int userId) {
        checkReviewAndUser(reviewId, userId);
        reviewStorage.removeLike(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    @Transactional
    public Review removeDislike(Long reviewId, int userId) {
        checkReviewAndUser(reviewId, userId);
        reviewStorage.removeDislike(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    private void checkReviewAndUser(Long reviewId, int userId) {
        if (!userService.userExists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (reviewStorage.getReviewById(reviewId) == null) {
            throw new NotFoundException("Отзыв не найден");
        }
    }
}