package com.ticketland.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketland.controllers.BookingController;
import com.ticketland.entities.User;
import com.ticketland.facades.BookingFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AppTestConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingFacade bookingFacade;

    @Autowired
    private BookingController bookingController;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testGetUsers() throws Exception {
        User user1 = new User("user1", "user 1", "user1@outlook.es");
        User user2 = new User("user2", "user 2", "user2@outlook.es");
        List<User> userAccounts = Arrays.asList(user1, user2);

        userAccounts.forEach(u -> bookingFacade.createUser(u));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("user1"))
                .andExpect(jsonPath("$[1].id").value("user2"));
    }

    @Test
    public void testGetUserById() throws Exception {

        bookingFacade.createUser(new User("user1", "user 1", "user1@outlook.es"));

        mockMvc.perform(get("/users/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user1"));

        mockMvc.perform(get("/users/user10"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with id: user10"));
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User("user4", "John Doe", "john@example.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user4"));
    }
}