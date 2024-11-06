package com.ticketland.jms;

import com.ticketland.exceptions.InsufficientFundsException;
import com.ticketland.facades.BookingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JmsConsumer {

    public static final Logger logger = LoggerFactory.getLogger(JmsProducer.class);

    private final BookingFacade bookingFacade;

    public JmsConsumer(BookingFacade bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @JmsListener(destination = "${spring.custom.booking.queue}")
    public void receiveMessage(BookingMessage message) {
        try {
            bookingFacade.bookTicket(
                    message.getUserId(),
                    message.getEventId()
            );
        } catch (InsufficientFundsException e) {
            logger.error("Insufficient funds from user " + message.getUserId() + " while booking a ticket for event " + message.getEventId());
        } catch (Throwable e) {
            logger.error("Unexpected error: " + e.getMessage());
        }
    }
}

