package com.ticketland.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class JmsProducer {

    public static final Logger logger = LoggerFactory.getLogger(JmsProducer.class);

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
            logger.error("Error while converting and sengind booking message: " + e.getMessage());
        }
    }
}

