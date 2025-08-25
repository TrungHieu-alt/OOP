package models;

import javafx.scene.control.CheckBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    // Concrete subclass for testing purposes
    static class TestUser extends User {
        public TestUser(int id, String fname, String lname, String dateOfBirth, String email, String username, String password, String imagePath) {
            super(id, fname, lname, dateOfBirth, email, username, password, imagePath);
        }

        @Override
        public String getRole() {
            return "TestRole";
        }
    }

    @BeforeEach
    void setUp() {
        user = new TestUser(1, "John", "Doe", "1990-01-01", "john@example.com", "johndoe", "password123", "/images/avatar.png");
    }

    @Test
    void testGetId() {
        assertEquals(1, user.getId(), "The ID should be 1.");
    }

    @Test
    void testGetAndSetFName() {
        assertEquals("John", user.getFName(), "The first name should be 'John'.");
        user.setFName("Jane");
        assertEquals("Jane", user.getFName(), "The first name should be updated to 'Jane'.");
    }

    @Test
    void testGetAndSetLName() {
        assertEquals("Doe", user.getLname(), "The last name should be 'Doe'.");
        user.setLname("Smith");
        assertEquals("Smith", user.getLname(), "The last name should be updated to 'Smith'.");
    }

    @Test
    void testGetAndSetDateOfBirth() {
        assertEquals("1990-01-01", user.getDateOfBirth(), "The date of birth should be '1990-01-01'.");
        user.setDateOfBirth("1995-05-05");
        assertEquals("1995-05-05", user.getDateOfBirth(), "The date of birth should be updated to '1995-05-05'.");
    }

    @Test
    void testGetAndSetEmail() {
        assertEquals("john@example.com", user.getEmail(), "The email should be 'john@example.com'.");
        user.setEmail("jane@example.com");
        assertEquals("jane@example.com", user.getEmail(), "The email should be updated to 'jane@example.com'.");
    }

    @Test
    void testGetAndSetUsername() {
        assertEquals("johndoe", user.getUsername(), "The username should be 'johndoe'.");
        user.setUsername("janedoe");
        assertEquals("janedoe", user.getUsername(), "The username should be updated to 'janedoe'.");
    }

    @Test
    void testGetAndSetPassword() {
        assertEquals("password123", user.getPassword(), "The password should be 'password123'.");
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword(), "The password should be updated to 'newpassword'.");
    }

    @Test
    void testGetAndSetImagePath() {
        assertEquals("/images/avatar.png", user.getImagePath(), "The image path should be '/images/avatar.png'.");
        user.setImagePath("/images/new_avatar.png");
        assertEquals("/images/new_avatar.png", user.getImagePath(), "The image path should be updated to '/images/new_avatar.png'.");
    }

    @Test
    void testGetAndSetSelected() {
        CheckBox checkBox = user.getSelected();
        assertFalse(checkBox.isSelected(), "The CheckBox should not be selected by default.");
        user.setSelected(true);
        assertTrue(checkBox.isSelected(), "The CheckBox should be selected.");
    }

    @Test
    void testGetName() {
        assertEquals("John Doe", user.getName(), "The full name should be 'John Doe'.");
    }

    @Test
    void testGetRole() {
        assertEquals("TestRole", user.getRole(), "The role should be 'TestRole'.");
    }
}