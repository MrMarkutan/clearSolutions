package clearsolutions.testassignment.service;

import clearsolutions.testassignment.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class UserService {

    private Map<Integer, User> users = new HashMap<>();

    private final AtomicInteger userId = new AtomicInteger();

    public UserService() {
    }

    //FOR TESTS ONLY
    public void setUsers(Map<Integer, User> users) {
        this.users = users;
    }

    //FOR TESTS ONLY
    protected Map<Integer, User> getUsersMap() {
        return users;
    }


    public User createUser(User user) {
        users.put(userId.getAndIncrement(), user);
        return user;
    }

    public User updateUserFields(Integer userId, User userUpdates) {
        return users.entrySet().stream()
                .filter(id -> id.getKey().equals(userId))
                .findFirst()
                .map(user -> {
                    User existingUser = user.getValue();
                    if (userUpdates.getEmail() != null) {
                        existingUser.setEmail(userUpdates.getEmail());
                    }
                    if (userUpdates.getFirstName() != null) {
                        existingUser.setFirstName(userUpdates.getFirstName());
                    }
                    if (userUpdates.getLastName() != null) {
                        existingUser.setLastName(userUpdates.getLastName());
                    }
                    if (userUpdates.getBirthDate() != null) {
                        existingUser.setBirthDate(userUpdates.getBirthDate());
                    }
                    if (userUpdates.getAddress() != null) {
                        existingUser.setAddress(userUpdates.getAddress());
                    }
                    if (userUpdates.getPhoneNumber() != null) {
                        existingUser.setPhoneNumber(userUpdates.getPhoneNumber());
                    }
                    return existingUser;
                })
                .orElse(null);
    }

    public User updateAllUserFields(Integer userId, User userUpdates) {
        return users.entrySet().stream()
                .filter(id -> id.getKey().equals(userId))
                .findFirst()
                .map(entry -> {
                    User existingUser = entry.getValue();
                    existingUser.setEmail(userUpdates.getEmail());
                    existingUser.setFirstName(userUpdates.getFirstName());
                    existingUser.setLastName(userUpdates.getLastName());
                    existingUser.setBirthDate(userUpdates.getBirthDate());
                    existingUser.setAddress(userUpdates.getAddress());
                    existingUser.setPhoneNumber(userUpdates.getPhoneNumber());
                    return existingUser;
                })
                .orElse(null);
    }

    public void deleteUser(Integer userId) {
        users.remove(userId);
    }

    public List<User> searchUsersByBirthDateRange(Date from, Date to) {
        return users.values().stream()
                .filter(user -> user.getBirthDate().after(from) && user.getBirthDate().before(to))
                .collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(Integer userId) {
        return users.get(userId);
    }
}
