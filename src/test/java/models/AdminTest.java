package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import services.BookService;
import services.UserService;
import services.LoanService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminTest {

    private Admin admin;
    private BookService mockBookService;
    private UserService mockUserService;
    private LoanService mockLoanService;

    @BeforeEach
    void setUp() {
        // Admin constructor inherited from User
        admin = new Admin(1, "adminUser", "password123");

        // Mock services
        mockBookService = Mockito.mock(BookService.class);
        mockUserService = Mockito.mock(UserService.class);
        mockLoanService = Mockito.mock(LoanService.class);

        // Inject mocks
        admin.setBookService(mockBookService);
        admin.setUserService(mockUserService);
        admin.setLoanService(mockLoanService);
    }

    // ---------- Tests for User class fields ----------
    @Test
    void testUserPropertiesInitialization() {
        assertEquals(1, admin.getId());          // inherited from User
        assertEquals("adminUser", admin.getUsername());
        assertEquals("password123", admin.getPassword());

        // Defaults should be null before setting
        assertNull(admin.getLname());
        assertNull(admin.getEmail());
    }

    @Test
    void testUserSettersAndGetters() {
        admin.setLname("Doe");
        admin.setDateOfBirth("2000-01-01");
        admin.setEmail("john.doe@example.com");

        assertEquals("Doe", admin.getLname());
        assertEquals("2000-01-01", admin.getDateOfBirth());
        assertEquals("john.doe@example.com", admin.getEmail());
    }

}
