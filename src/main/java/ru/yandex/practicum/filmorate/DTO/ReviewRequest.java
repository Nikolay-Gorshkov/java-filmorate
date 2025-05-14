package ru.yandex.practicum.filmorate.DTO;

import lombok.Getter;

public class ReviewRequest {
    @Getter
    private Long reviewId;
    @Getter
    private String content;
    @Getter
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIsPositive(Boolean isPositive) {
        this.isPositive = isPositive;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }
}