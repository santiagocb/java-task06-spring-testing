package com.ticketland.services;

import com.ticketland.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testRegisterUser() {
        User user = new User("user123", "John Doe", "john@example.com");
        User registeredUser = userService.register(user);
        assertNotNull(registeredUser.getId());
        assertEquals("John Doe", registeredUser.getName());
        assertEquals("john@example.com", registeredUser.getEmail());

        User fetchedUser = userService.getById(registeredUser.getId());
        assertEquals(registeredUser, fetchedUser);
    }

    @Test
    public void testGetById_Success() {
        User user = new User("user123", "John Doe", "john@example.com");
        userService.register(user);

        User foundUser = userService.getById(user.getId());
        assertNotNull(foundUser);
        assertEquals("John Doe", foundUser.getName());
        assertEquals("john@example.com", foundUser.getEmail());
    }

    @Test
    public void testGetById_NotFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.getById("nonexistent123"));
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User("user124", "John Doe", "john@example.com");
        User user2 = new User("user125", "Jane Doe", "jane@example.com");
        userService.register(user1);
        userService.register(user2);

        var users = userService.getAll();
        assertNotNull(users);
        assertTrue(users.size() >= 2);
    }
}