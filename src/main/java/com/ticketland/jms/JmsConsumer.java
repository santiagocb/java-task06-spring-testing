package com.ticketland.jms;

import com.ticketland.facades.BookingFacade;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JmsConsumer {

    private final BookingFacade bookingFacade;

    public JmsConsumer(BookingFacade bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @JmsListener(destination = "${spring.custom.booking.queue}")
    public void receiveMessage(BookingMessage message) {
        bookingFacade.bookTicket(
                message.getUserId(),
                message.getEventId()
        );
    }
}

