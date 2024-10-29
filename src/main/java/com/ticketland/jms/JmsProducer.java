package com.ticketland.jms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class JmsProducer {

    @Value("${spring.custom.booking.queue}")
    private String bookingQueueName;

    private final JmsTemplate jmsTemplate;

    public JmsProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendBookingMessage(BookingMessage message) {
        try {
            jmsTemplate.convertAndSend(bookingQueueName, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

