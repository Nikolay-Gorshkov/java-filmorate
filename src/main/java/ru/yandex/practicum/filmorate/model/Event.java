package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Data
@RequiredArgsConstructor
public class Event {

    private int eventId;
    private int userId;
    private String eventType;
    private String operation;
    private int entityId;
    private Timestamp timestamp;

}
