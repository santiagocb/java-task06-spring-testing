package com.ticketland.jms;

import java.io.Serializable;

public class BookingMessage implements Serializable {

    private String userId;
    private String eventId;

    public BookingMessage(){}

    public BookingMessage(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}

