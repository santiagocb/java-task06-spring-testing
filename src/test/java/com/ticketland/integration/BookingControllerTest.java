package com.ticketland.integration;

import com.ticketland.controllers.BookingController;
import com.ticketland.entities.Event;
import com.ticketland.entities.Ticket;
import com.ticketland.entities.User;
import com.ticketland.entities.UserAccount;
import com.ticketland.exceptions.InsufficientFundsException;
import com.ticketland.facades.BookingFacade;
import com.ticketland.jms.JmsConsumer;
import com.ticketland.jms.JmsProducer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AppTestConfig.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private BookingFacade bookingFacade;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private JmsProducer jmsProducer;

    @Autowired
    private JmsConsumer jmsConsumer;

    @Test
    public void testBookTicket() throws Exception {

        User user = new User("user-test-01", "John Doe", "john@example.com");
        Event event = new Event("event123", "Concert", "Main Arena", LocalDate.now(), 50.0);

        bookingFacade.createEvent(event);
        bookingFacade.createUser(user);
        bookingFacade.refillAccount(user.getId(), 100);

        mockMvc.perform(post("/tickets/booking")
                        .param("userId", "user-test-01")
                        .param("eventId", "event123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value("user-test-01"))
                .andExpect(jsonPath("$.event.id").value("event123"))
                .andExpect(jsonPath("$.event.place").value("Main Arena"));

        List<Ticket> tickets = bookingFacade.getTicketsByUserAccountId(user.getId());
        assertEquals(1, tickets.size());
        var ticket = tickets.get(0);
        assertEquals("Main Arena", ticket.getEvent().getPlace());
    }

    @Test
    public void testBookTicket_InsufficientFunds() throws Exception {

        User user = new User("user-test-02", "John Doe", "john@example.com");
        Event event = new Event("event124", "Concert", "Main Arena", LocalDate.now(), 50.0);

        bookingFacade.createEvent(event);
        bookingFacade.createUser(user);

        mockMvc.perform(post("/tickets/booking")
                        .param("userId", "user-test-02")
                        .param("eventId", "event124"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User with id: user-test-02 has insufficient funds"));
    }

    @Test
    public void testBookTicketAsync() throws Exception {

        User user = new User("user-test-09", "John Doe", "john@example.com");
        Event event = new Event("event125", "Concert", "Main Arena", LocalDate.now(), 50.0);

        bookingFacade.createEvent(event);
        bookingFacade.createUser(user);
        bookingFacade.refillAccount(user.getId(), 100);

        mockMvc.perform(post("/tickets/async-booking")
                        .param("userId", "user-test-09")
                        .param("eventId", "event125"))
                .andExpect(status().isAccepted());

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Ticket> tickets = bookingFacade.getTicketsByUserAccountId(user.getId());
            assertEquals(1, tickets.size());
            var ticket = tickets.get(0);
            assertEquals("Main Arena", ticket.getEvent().getPlace());
        });
    }

    @Test
    public void testBookTicketAsync_UserNotFound() throws Exception {

        mockMvc.perform(post("/tickets/async-booking")
                        .param("userId", "nonexistentuser")
                        .param("eventId", "event123"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with id: nonexistentuser"));
    }
}