package com.ticketland.controllers;

import com.ticketland.entities.User;
import com.ticketland.entities.UserAccount;
import com.ticketland.facades.BookingFacade;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final BookingFacade bookingFacade;

    public UserController(BookingFacade bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserAccount>> getUsers() {
        return ResponseEntity.ok(bookingFacade.getAllUserAccounts());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(bookingFacade.getUserAccount(userId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    @PostMapping("/users")
    public ResponseEntity<UserAccount> createUser(@RequestBody User user) {
        return ResponseEntity.ok(bookingFacade.createUser(user));
    }

    @PostMapping("/accounts/refill")
    public ResponseEntity<String> refillUserAccount(@RequestParam String userId, @RequestParam double amount) {
        try {
            bookingFacade.refillAccount(userId, amount);
            return ResponseEntity.status(HttpStatus.OK).body("UserAccount refilled with: " + amount);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }
}
