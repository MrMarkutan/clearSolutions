package clearsolutions.testassignment.service;

import clearsolutions.testassignment.model.User;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserService();
        userService.setUsers(new HashMap<>());
    }

    private static User createValidUser() throws ParseException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"));
        user.setAddress("123 Main St");
        user.setPhoneNumber("1234567890");
        return user;
    }

    private void saveUser(User existingUser) {
        userService.createUser(existingUser);
    }

    @Test
    public void testCreateUser() throws ParseException {
        User user = createValidUser();

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user, createdUser);

        // Verify that the user was added to the map
        Map<Integer, User> usersMap = userService.getUsersMap();
        assertEquals(1, usersMap.size());
        assertTrue(usersMap.containsValue(createdUser));
    }

    @Test
    public void testUpdateUserFields() throws ParseException {
        User existingUser = createValidUser();
        existingUser.setEmail("existing@example.com");

        saveUser(existingUser);

        User userUpdates = new User();
        userUpdates.setEmail("updated@example.com");

        User updatedUser = userService.updateUserFields(0, userUpdates);

        assertNotNull(updatedUser);

        Map<Integer, User> usersMap = userService.getUsersMap();
        assertEquals(1, usersMap.size());
        assertTrue(usersMap.containsValue(updatedUser));
        assertEquals(userUpdates.getEmail(), usersMap.get(0).getEmail());
    }

    @Test
    public void testUpdateAllUserFields() throws ParseException {
        User existingUser = createValidUser();

        saveUser(existingUser);

        User userUpdates = new User();
        userUpdates.setEmail("updated@example.com");
        userUpdates.setFirstName("Jack");
        userUpdates.setLastName("Black");
        userUpdates.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("1995-02-15"));

        User updatedUser = userService.updateAllUserFields(0, userUpdates);

        assertNotNull(updatedUser);
        Map<Integer, User> usersMap = userService.getUsersMap();
        assertEquals(1, usersMap.size());
        assertTrue(usersMap.containsValue(updatedUser));
        assertEquals(userUpdates, usersMap.get(0));
    }

    @Test
    public void testDeleteUser() throws ParseException {
        saveUser(createValidUser()); // Create a user for deletion

        userService.deleteUser(0);

        assertEquals(0, userService.getAllUsers().size());
    }

    @Test
    public void testSearchUsersByBirthDateRange() throws ParseException {
        User user1 = createValidUser();
        user1.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("1992-02-14"));
        User user2 = createValidUser();
        user2.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("1992-06-21"));

        saveUser(user1);
        saveUser(user2);

        List<User> result = userService.searchUsersByBirthDateRange(
                new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"),
                new SimpleDateFormat("yyyy-MM-dd").parse("1995-02-15")
        );

        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    public void testGetAllUsers() throws ParseException {
        User user1 = createValidUser();
        saveUser(user1);
        User user2 = createValidUser();
        saveUser(user2);


        List<User> allUsers = userService.getAllUsers();

        assertEquals(2, allUsers.size());

        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void testGetUserById() throws ParseException {
        User user = createValidUser();

        userService.createUser(user);

        User fetchedUser = userService.getUserById(0);

        assertNotNull(fetchedUser);

        assertEquals(user, fetchedUser);
    }
}
