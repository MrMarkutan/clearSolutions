package clearsolutions.testassignment.controller;

import clearsolutions.testassignment.model.User;
import clearsolutions.testassignment.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Value("${user.min.age}")
    private int userAgeRestriction;

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"));

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    public void testCreateUserWithLowerAgeRestriction() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01")); // Younger than age restriction

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User age is lower then " + userAgeRestriction));

        verifyNoInteractions(userService);
    }

    @Test
    public void testUpdateUserFields() throws Exception {
        Integer userId = 1;
        User userUpdates = new User();
        userUpdates.setEmail("updated@example.com");

        when(userService.updateUserFields(eq(userId), any(User.class))).thenReturn(userUpdates);

        mockMvc.perform(put("/api/user/update/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
        verify(userService, times(1)).updateUserFields(eq(userId), any(User.class));
    }

    @Test
    public void testUpdateAllUserFields() throws Exception {
        Integer userId = 1;
        User userUpdates = new User();
        userUpdates.setEmail("updated@example.com");
        userUpdates.setFirstName("updatedFirstName");
        userUpdates.setLastName("updatedLastName");
        userUpdates.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"));

        when(userService.updateAllUserFields(eq(userId), any(User.class))).thenReturn(userUpdates);

        mockMvc.perform(put("/api/user/updateAll/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService, times(1)).updateAllUserFields(eq(userId), any(User.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Integer userId = 1;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/user/delete/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    public void testSearchUsersByBirthDateRange() throws Exception {
        Date from = new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01");
        Date to = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");

        User user1 = new User();
        user1.setEmail("user1@example.com");
        User user2 = new User();
        user2.setEmail("user2@example.com");

        when(userService.searchUsersByBirthDateRange(eq(from), eq(to))).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/user/searchByBirthDate")
                        .param("from", "1990-01-01")
                        .param("to", "2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));

        verify(userService, times(1)).searchUsersByBirthDateRange(eq(from), eq(to));
    }

    @Test
    public void testSearchUsersByBadTimeRange() throws Exception {
        Date from = new SimpleDateFormat("yyyy-MM-dd").parse("2022-01-01");
        Date to = new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01");

        mockMvc.perform(get("/api/user/searchByBirthDate")
                        .param("from", new SimpleDateFormat("yyyy-MM-dd").format(from))
                        .param("to", new SimpleDateFormat("yyyy-MM-dd").format(to)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Bad time range"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        User user2 = new User();
        user2.setEmail("user2@example.com");

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUserById() throws Exception {
        Integer userId = 1;
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userService.getUserById(eq(userId))).thenReturn(user);

        mockMvc.perform(get("/api/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
        verify(userService, times(1)).getUserById(eq(userId));
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {

        when(userService.getUserById(anyInt())).thenReturn(null);

        mockMvc.perform(get("/api/user/{userId}", 0))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("User not found with id: " + 0));
        verify(userService, times(1)).getUserById(anyInt());
    }
}
