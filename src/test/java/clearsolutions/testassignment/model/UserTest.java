package clearsolutions.testassignment.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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

    @Test
    public void testValidUser() throws ParseException {
        User user = createValidUser();


        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testRequiredUserFields() {
        User user = new User();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
        assertEquals(4, constraintViolations.size());
        List<String> errorMessages = new ArrayList<>();
        constraintViolations
                .forEach(violation -> errorMessages.add(violation.getMessage()));

        assertTrue(errorMessages.contains("First name is required"));
        assertTrue(errorMessages.contains("Last name is required"));
        assertTrue(errorMessages.contains("Birth date is required"));
        assertTrue(errorMessages.contains("Email is required"));
    }

    @Test
    void testInvalidEmail() throws ParseException {
        User user = createValidUser();
        user.setEmail("bad-email");

        Set<ConstraintViolation<User>> validate = validator.validate(user);
        assertFalse(validate.isEmpty());
        assertEquals(1, validate.size());

        ConstraintViolation<User> violation = validate.iterator().next();
        assertEquals("Invalid email format", violation.getMessage());
    }

    @Test
    void testBirthDateInPast() throws ParseException {
        User user = createValidUser();

        user.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("2030-01-30"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Birth date must be in past", violation.getMessage());
    }

    @Test
    void testInvalidPhoneNumber() throws ParseException {
        User user = createValidUser();
        user.setPhoneNumber("invalid-phone-number");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Invalid phone number", violation.getMessage());
    }
}