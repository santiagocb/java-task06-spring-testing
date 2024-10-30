package com.ticketland.controllers;

import com.ticketland.exceptions.InsufficientFundsException;
import com.ticketland.facades.BookingFacade;
import com.ticketland.jms.BookingMessage;
import com.ticketland.jms.JmsProducer;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    private final BookingFacade bookingFacade;

    private final JmsProducer jmsProducer;

    public BookingController(BookingFacade bookingFacade, JmsProducer jmsProducer) {
        this.bookingFacade = bookingFacade;
        this.jmsProducer = jmsProducer;
    }

    @PostMapping("/tickets/booking")
    public ResponseEntity<?> bookTicket(@RequestParam String userId, @RequestParam String eventId) {
        try {
            return ResponseEntity.ok(bookingFacade.bookTicket(userId, eventId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + userId);
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with id: " + userId + " has insufficient funds");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    @PostMapping("/tickets/async-booking")
    public ResponseEntity<?> bookTicketAsync(@RequestParam String userId, @RequestParam String eventId) {
        try {
            bookingFacade.getUserAccount(userId);
            jmsProducer.sendBookingMessage(new BookingMessage(userId, eventId));
            return ResponseEntity.accepted().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }
}
