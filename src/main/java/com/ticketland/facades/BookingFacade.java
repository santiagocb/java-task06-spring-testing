package com.ticketland.facades;

import com.ticketland.entities.Event;
import com.ticketland.entities.Ticket;
import com.ticketland.entities.User;
import com.ticketland.entities.UserAccount;
import com.ticketland.exceptions.InsufficientFundsException;
import com.ticketland.services.EventService;
import com.ticketland.services.TicketService;
import com.ticketland.services.UserAccountService;
import com.ticketland.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingFacade {

    private final UserService userService;
    private final UserAccountService userAccountService;
    private final EventService eventService;
    private final TicketService ticketService;

    public BookingFacade(UserService userService, UserAccountService userAccountService, EventService eventService, TicketService ticketService) {
        this.userAccountService = userAccountService;
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    public UserAccount createUser(User user) {
        userService.register(user);
        return userAccountService.createAccount(user.getId());
    }

    public UserAccount getUserAccount(String userId) {
        return userAccountService.findByUserId(userId);
    }

    public Ticket bookTicket(String userId, String eventId) throws InsufficientFundsException {
        UserAccount account = userAccountService.findByUserId(userId);
        Event event = eventService.findByEventId(eventId);

        if (account.getBalance() < event.getTicketPrice()) {
            throw new InsufficientFundsException("Insufficient funds to book the ticket.");
        }
        userAccountService.refillBalance(userId, event.getTicketPrice() * -1);

        return ticketService.generate(new Ticket(account, event));
    }

    public void refillAccount(String userId, double amount) {
        userAccountService.refillBalance(userId, amount);
    }

    public List<UserAccount> getAllUserAccounts() {
        return userAccountService.findAll();
    }

    public List<Ticket> getAllTickets() {
        return ticketService.findAll();
    }

    public Event createEvent(Event event) {
        return eventService.create(event);
    }

    public Event getEventById(String eventId) {
        return eventService.findByEventId(eventId);
    }

    public List<Event> getAllEvents() {
        return eventService.findAll();
    }

    public List<Ticket> getTicketsByUserAccountId(String userAccountId) {
        return ticketService.findTicketsByAccountUserId(userAccountId);
    }

    public List<Ticket> getBookedTickets(UserAccount userAccount, int pageSize, int pageNum) {
        return ticketService.getBookedTickets(userAccount, pageSize, pageNum);
    }

    public void preloadTickets(List<Ticket> tickets) {
        ticketService.preloadTickets(tickets);
    }
}
