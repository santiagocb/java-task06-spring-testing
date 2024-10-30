package com.ticketland.services;

import com.ticketland.entities.Event;
import jakarta.persistence.EntityNotFoundException;
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
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Test
    public void testCreateEvent() {
        String eventId = UUID.randomUUID().toString();
        Event event = new Event(eventId, "Concert", "Main Arena", LocalDate.now(), 100.0);
        Event createdEvent = eventService.create(event);
        assertEquals(eventId, createdEvent.getId());
        assertEquals("Concert", createdEvent.getName());
        assertEquals("Main Arena", createdEvent.getPlace());
        assertEquals(LocalDate.now(), createdEvent.getDate());
        assertEquals(100.0, createdEvent.getTicketPrice());

        Event fetchedEvent = eventService.findByEventId(eventId);
        assertEquals(createdEvent, fetchedEvent);
    }

    @Test
    public void testUpdateTicketPrice() {
        String eventId = UUID.randomUUID().toString();
        Event event = new Event(eventId, "Concert", "Main Arena", LocalDate.now(), 100.0);
        Event createdEvent = eventService.create(event);
        eventService.updateTicketPrice(eventId, 150.0);

        Event updatedEvent = eventService.findByEventId(eventId);
        assertEquals(150.0, updatedEvent.getTicketPrice());
    }

    @Test
    public void testFindByEventId_NotFound() {
        String randomEventId = UUID.randomUUID().toString();
        assertThrows(EntityNotFoundException.class, () -> eventService.findByEventId(randomEventId));
    }

    @Test
    public void testFindAllEvents() {
        String eventId1 = UUID.randomUUID().toString();
        String eventId2 = UUID.randomUUID().toString();
        Event event1 = new Event(eventId1, "Concert", "Main Arena", LocalDate.now(), 100.0);
        Event event2 = new Event(eventId2, "Festival", "Outdoor Park", LocalDate.now().plusDays(1), 200.0);
        eventService.create(event1);
        eventService.create(event2);

        List<Event> events = eventService.findAll();
        assertNotNull(events);
        assertTrue(events.size() >= 2);
        assertTrue(events.stream().anyMatch(e -> e.getId().equals(eventId1)));
        assertTrue(events.stream().anyMatch(e -> e.getId().equals(eventId2)));
    }
}