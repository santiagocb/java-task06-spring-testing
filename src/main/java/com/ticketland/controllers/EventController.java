package com.ticketland.controllers;

import com.ticketland.entities.Event;
import com.ticketland.facades.BookingFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class EventController {

    private final BookingFacade bookingFacade;


    public EventController(BookingFacade bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getEvents() {
        return ResponseEntity.ok(bookingFacade.getAllEvents());
    }

    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(Event event) {
        return ResponseEntity.ok(bookingFacade.createEvent(event));
    }
}
