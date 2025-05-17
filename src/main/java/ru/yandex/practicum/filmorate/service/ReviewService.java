package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.DTO.ReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
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
        if (request.getUserId() == null) {
            throw new ValidationException("User ID не может быть null");
        }
        if (request.getFilmId() == null) {
            throw new ValidationException("Film ID не может быть null");
        }
        userService.getUserById(request.getUserId());
        filmService.getFilmById(request.getFilmId());


        if (request.getReviewId() != null) {
            throw new ValidationException("Для создания отзыва поле reviewId должно быть null");
        }

        userService.getUserById(request.getUserId());

        if (!filmService.filmExists(request.getFilmId())) {
            throw new NotFoundException("Фильм с id " + request.getFilmId() + " не найден");
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
        Review review = ReviewMapper.toReview(request);
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
            throw new NotFoundException("Отзыв с id " + request.getReviewId() + " не найден");
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
        if (review == null) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден");
        }
        reviewStorage.deleteReview(reviewId);
    }

    public Review getReviewById(Long reviewId) {
        Review review = reviewStorage.getReviewById(reviewId);
        if (review == null) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден");
        }
        return review;
    }

    public List<Review> getReviews(Integer filmId, int count) {
        return reviewStorage.getReviews(filmId, count);
    }

    @Transactional
    public Review addLike(Long reviewId, int userId) {
        checkUserExists(userId);
        checkReviewExists(reviewId);
        reviewStorage.addLike(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    @Transactional
    public Review addDislike(Long reviewId, int userId) {
        checkUserExists(userId);
        checkReviewExists(reviewId);
        reviewStorage.addDislike(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    @Transactional
    public Review removeLike(Long reviewId, int userId) {
        checkUserExists(userId);
        checkReviewExists(reviewId);
        reviewStorage.removeLike(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    @Transactional
    public Review removeDislike(Long reviewId, int userId) {
        checkUserExists(userId);
        checkReviewExists(reviewId);
        reviewStorage.removeDislike(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    private void checkUserExists(int userId) {
        if (userService.getUserById(userId) == null){
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    private void checkReviewExists(Long reviewId) {
        if (reviewStorage.getReviewById(reviewId) == null) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден");
        }
    }
}