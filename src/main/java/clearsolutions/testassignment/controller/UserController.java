package clearsolutions.testassignment.controller;

import clearsolutions.testassignment.model.User;
import clearsolutions.testassignment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Value("${user.min.age}")
    private int userAgeRestriction;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {

        Date today = new Date();
        long ageInMillis = today.getTime() - user.getBirthDate().getTime();
        long ageInYears = ageInMillis / (1000L * 60 * 60 * 24 * 365);
        if (ageInYears < userAgeRestriction) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User age is lower then " + userAgeRestriction);
        }
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }


    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateUserFields(@PathVariable Integer userId, @RequestBody User userUpdates) {
        User updatedUser = userService.updateUserFields(userId, userUpdates);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/updateAll/{userId}")
    public ResponseEntity<User> updateAllUserFields(@PathVariable Integer userId, @Valid @RequestBody User userUpdates) {
        User updatedUser = userService.updateAllUserFields(userId, userUpdates);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searchByBirthDate")
    public ResponseEntity<?> searchUsersByBirthDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        if (from.after(to)) {
            return ResponseEntity.badRequest().body("Bad time range");
        }

        List<User> users = userService.searchUsersByBirthDateRange(from, to);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> all = userService.getAllUsers();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + userId);
        }
    }
}
