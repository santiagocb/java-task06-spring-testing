package com.ticketland.controllers;

import com.ticketland.facades.BookingFacade;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    private final BookingFacade bookingFacade;

    public BookingController(BookingFacade bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @PostMapping("/tickets/booking")
    public String bookTicket(@RequestParam String userId, @RequestParam String eventId) {
        bookingFacade.bookTicket(userId, eventId);
        return "redirect:/tickets?success";
    }

    @PostMapping("/tickets/async-booking")
    public String bookTicketAsync(@RequestParam String userId, @RequestParam String eventId) {
        bookingFacade.bookTicket(userId, eventId);
        return "redirect:/tickets?success";
    }
}
