package repository;

import exceptions.DatabaseException;
import models.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private static Connection connection;

    @BeforeAll
    static void setupDatabase() throws Exception {
        // Set up an in-memory H2 database for testing
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement statement = connection.createStatement();

        // Create the `userdetail` table
        statement.execute("CREATE TABLE userdetail (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "fName VARCHAR(255), " +
                "lName VARCHAR(255), " +
                "date_of_birth VARCHAR(255), " +
                "email VARCHAR(255), " +
                "username VARCHAR(255), " +
                "password VARCHAR(255), " +
                "avatar_path VARCHAR(255), " +
                "role VARCHAR(50))");

        // Insert sample data
        statement.execute("INSERT INTO userdetail (id, fName, lName, date_of_birth, email, username, password, avatar_path, role) " +
                "VALUES (1, 'John', 'Doe', '1990-01-01', 'john@example.com', 'johndoe', 'password123', '/images/avatar1.png', 'Member')");
        statement.execute("INSERT INTO userdetail (id, fName, lName, date_of_birth, email, username, password, avatar_path, role) " +
                "VALUES (2, 'Jane', 'Doe', '1992-02-02', 'jane@example.com', 'janedoe', 'password456', '/images/avatar2.png', 'Admin')");

        // Set the mock connection for the repository
        DatabaseConnection.setMockConnection(connection);
    }

    @AfterAll
    static void tearDownDatabase() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void testCountUserRecords() throws DatabaseException {
        int count = UserRepository.countUserRecords();
        assertEquals(2, count, "The user count should be 2.");
    }

    @Test
    void testCreateNewUser() throws Exception {
        User newUser = new User();
        newUser.setFName("Alice");
        newUser.setLname("Smith");
        newUser.setDateOfBirth("1995-03-03");
        newUser.setEmail("alice@example.com");
        newUser.setUsername("alicesmith");
        newUser.setPassword("password789");
        newUser.setImagePath("/images/avatar3.png");

        boolean isCreated = UserRepository.createNewUser(newUser);
        assertTrue(isCreated, "The user should be created successfully.");

        int count = UserRepository.countUserRecords();
        assertEquals(3, count, "The user count should now be 3.");
    }

    @Test
    void testDeleteUser() throws DatabaseException {
        UserRepository.deleteUser(1);

        int count = UserRepository.countUserRecords();
        assertEquals(1, count, "The user count should now be 1.");
    }

    @Test
    void testUpdateUser() throws DatabaseException {
        User userToUpdate = new User();
        userToUpdate.setId(2);
        userToUpdate.setFName("Jane Updated");
        userToUpdate.setLname("Doe Updated");
        userToUpdate.setDateOfBirth("1992-02-22");
        userToUpdate.setEmail("jane.updated@example.com");

        UserRepository.updateUser(userToUpdate);

        User updatedUser = UserRepository.getUserByUsername("janedoe");
        assertEquals("Jane Updated", updatedUser.getFName(), "The first name should be updated.");
        assertEquals("Doe Updated", updatedUser.getLname(), "The last name should be updated.");
        assertEquals("1992-02-22", updatedUser.getDateOfBirth(), "The date of birth should be updated.");
        assertEquals("jane.updated@example.com", updatedUser.getEmail(), "The email should be updated.");
    }

    @Test
    void testDoesUserExist() throws DatabaseException {
        boolean exists = UserRepository.doesUserExist("johndoe", "john@example.com");
        assertTrue(exists, "The user should exist.");

        boolean notExists = UserRepository.doesUserExist("nonexistent", "nonexistent@example.com");
        assertFalse(notExists, "The user should not exist.");
    }

    @Test
    void testGetUserByUsername() throws DatabaseException {
        User user = UserRepository.getUserByUsername("johndoe");
        assertNotNull(user, "The user should be retrieved successfully.");
        assertEquals("John", user.getFName(), "The first name should match.");
        assertEquals("Doe", user.getLname(), "The last name should match.");
    }
}