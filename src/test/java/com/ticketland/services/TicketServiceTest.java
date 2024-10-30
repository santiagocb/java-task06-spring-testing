package com.ticketland.services;

import com.ticketland.entities.Event;
import com.ticketland.entities.Ticket;
import com.ticketland.entities.User;
import com.ticketland.entities.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserService userService;

    @Test
    public void testGenerateTicket() {
        User user = new User("user123", "John Doe", "john@example.com");
        userService.register(user);
        UserAccount userAccount = userAccountService.createAccount(user.getId());
        Event event = new Event(UUID.randomUUID().toString(), "Concert", "Main Arena", LocalDate.now(), 100.0);
        eventService.create(event);
        Ticket ticket = new Ticket(userAccount, event);

        Ticket generatedTicket = ticketService.generate(ticket);
        assertNotNull(generatedTicket.getId());
        assertEquals(userAccount, generatedTicket.getUser());
    }

    @Test
    public void testFindTicketsByAccountUserId() {
        User user = new User("user01", "John Doe", "john@example.com");
        userService.register(user);
        UserAccount userAccount = userAccountService.createAccount(user.getId());
        Event event = new Event(UUID.randomUUID().toString(), "Concert", "Main Arena", LocalDate.now(), 100.0);
        eventService.create(event);
        Ticket ticket = new Ticket(userAccount, event);
        ticketService.generate(ticket);

        List<Ticket> tickets = ticketService.findTicketsByAccountUserId(userAccount.getId());
        assertFalse(tickets.isEmpty());
    }

    @Test
    public void testFindAllTickets() {
        User user = new User("user02", "John Doe", "john@example.com");
        userService.register(user);
        UserAccount userAccount = userAccountService.createAccount(user.getId());
        Event event = new Event(UUID.randomUUID().toString(), "Concert", "Main Arena", LocalDate.now(), 100.0);
        eventService.create(event);
        Ticket ticket1 = new Ticket(userAccount, event);
        Ticket ticket2 = new Ticket(userAccount, event);
        ticketService.generate(ticket1);
        ticketService.generate(ticket2);

        List<Ticket> tickets = ticketService.findAll();
        assertTrue(tickets.size() >= 2);
    }

    @Test
    public void testGetBookedTickets() {
        User user = new User("user03", "John Doe", "john@example.com");
        userService.register(user);
        UserAccount userAccount = userAccountService.createAccount(user.getId());
        Event event = new Event(UUID.randomUUID().toString(), "Concert", "Main Arena", LocalDate.now(), 100.0);
        eventService.create(event);
        Ticket ticket1 = new Ticket(userAccount, event);
        Ticket ticket2 = new Ticket(userAccount, event);
        ticketService.generate(ticket1);
        ticketService.generate(ticket2);

        List<Ticket> bookedTickets = ticketService.getBookedTickets(userAccount, 10, 1);
        assertTrue(bookedTickets.size() >= 2);
    }
}