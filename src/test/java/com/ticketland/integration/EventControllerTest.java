package com.ticketland.integration;

import com.ticketland.controllers.BookingController;
import com.ticketland.entities.Event;
import com.ticketland.facades.BookingFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AppTestConfig.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingFacade bookingFacade;

    @Autowired
    private BookingController bookingController;

    @Test
    public void testGetEvents() throws Exception {
        Event event1 = new Event("event123", "Concert", "Main Arena", LocalDate.now(), 50.0);
        Event event2 = new Event("event124", "Theatre", "East Hall", LocalDate.now().plusDays(1), 75.0);
        List<Event> allEvents = Arrays.asList(event1, event2);

        allEvents.forEach(e -> bookingFacade.createEvent(e));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(event1.getId())))
                .andExpect(jsonPath("$[*].id", hasItem(event2.getId())));
    }
}