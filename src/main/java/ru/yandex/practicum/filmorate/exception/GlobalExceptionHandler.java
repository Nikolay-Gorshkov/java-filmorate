package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.DTO.ReviewResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ReviewResponse> handleValidationException(ValidationException e) {
        ReviewResponse errorResponse = new ReviewResponse();
        errorResponse.setContent(e.getMessage());
        errorResponse.setReviewId(null);
        errorResponse.setIsPositive(null);
        errorResponse.setUserId(0);
        errorResponse.setFilmId(0);
        errorResponse.setUseful(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ReviewResponse> handleNotFoundException(NotFoundException e) {
        ReviewResponse errorResponse = new ReviewResponse();
        errorResponse.setContent(e.getMessage());
        errorResponse.setReviewId(null);
        errorResponse.setIsPositive(null);
        errorResponse.setUserId(0);
        errorResponse.setFilmId(0);
        errorResponse.setUseful(null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ReviewResponse> handleMethodArgumentNotValidException(
            org.springframework.web.bind.MethodArgumentNotValidException e) {
        ReviewResponse errorResponse = new ReviewResponse();
        errorResponse.setContent("Validation error: " + e.getMessage());
        errorResponse.setReviewId(null);
        errorResponse.setIsPositive(null);
        errorResponse.setUserId(0);
        errorResponse.setFilmId(0);
        errorResponse.setUseful(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ReviewResponse> handleOtherExceptions(Exception e) {
        log.error("Internal Server Error", e);
        ReviewResponse errorResponse = new ReviewResponse();
        errorResponse.setContent("Internal Server Error");
        errorResponse.setReviewId(null);
        errorResponse.setIsPositive(null);
        errorResponse.setUserId(0);
        errorResponse.setFilmId(0);
        errorResponse.setUseful(null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ReviewResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ReviewResponse errorResponse = new ReviewResponse();
        errorResponse.setContent("Data integrity violation: " + e.getMessage());
        errorResponse.setReviewId(null);
        errorResponse.setIsPositive(null);
        errorResponse.setUserId(0);
        errorResponse.setFilmId(0);
        errorResponse.setUseful(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ReviewResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ReviewResponse errorResponse = new ReviewResponse();
        errorResponse.setContent("Validation error: " + e.getMessage());
        errorResponse.setReviewId(null);
        errorResponse.setIsPositive(null);
        errorResponse.setUserId(0);
        errorResponse.setFilmId(0);
        errorResponse.setUseful(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ReviewResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ReviewResponse errorResponse = new ReviewResponse();
        String message = (e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage());
        errorResponse.setContent("Validation error: " + message);
        errorResponse.setReviewId(null);
        errorResponse.setIsPositive(null);
        errorResponse.setUserId(0);
        errorResponse.setFilmId(0);
        errorResponse.setUseful(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}