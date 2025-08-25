package repository;

import models.Book;
import models.Loan;
import models.Member;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanRepositoryTest {

    private static Connection connection;

    @BeforeAll
    static void setupDatabase() throws Exception {
        // Set up an in-memory H2 database for testing
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement statement = connection.createStatement();

        // Create the `members` table
        statement.execute("CREATE TABLE members (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "email VARCHAR(255))");

        // Create the `books` table
        statement.execute("CREATE TABLE books (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255), " +
                "author VARCHAR(255), " +
                "isbn VARCHAR(255), " +
                "quantity INT)");

        // Create the `loans` table
        statement.execute("CREATE TABLE loans (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "member_id INT, " +
                "book_id INT, " +
                "issue_date DATE, " +
                "due_date DATE, " +
                "return_date DATE, " +
                "FOREIGN KEY (member_id) REFERENCES members(id), " +
                "FOREIGN KEY (book_id) REFERENCES books(id))");

        // Insert sample data into `members`
        statement.execute("INSERT INTO members (id, name, email) VALUES (1, 'John Doe', 'john@example.com')");
        statement.execute("INSERT INTO members (id, name, email) VALUES (2, 'Jane Doe', 'jane@example.com')");

        // Insert sample data into `books`
        statement.execute("INSERT INTO books (id, title, author, isbn, quantity) " +
                "VALUES (1, 'Effective Java', 'Joshua Bloch', '1234567890', 5)");
        statement.execute("INSERT INTO books (id, title, author, isbn, quantity) " +
                "VALUES (2, 'Clean Code', 'Robert C. Martin', '0987654321', 3)");

        // Insert sample data into `loans`
        statement.execute("INSERT INTO loans (id, member_id, book_id, issue_date, due_date) " +
                "VALUES (1, 1, 1, '2023-08-01', '2023-08-15')");
        statement.execute("INSERT INTO loans (id, member_id, book_id, issue_date, due_date, return_date) " +
                "VALUES (2, 2, 2, '2023-08-05', '2023-08-20', '2023-08-18')");

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
    void testCountLoanRecords() {
        int count = LoanRepository.countLoanRecords();
        assertEquals(2, count, "The loan count should be 2.");
    }

    @Test
    void testGetLoansByMemberId() throws Exception {
        List<Loan> loans = LoanRepository.getLoansByMemberId(1);
        assertEquals(1, loans.size(), "Member 1 should have 1 loan.");
        assertEquals("Effective Java", loans.get(0).getBook().getTitle(), "The loaned book should be 'Effective Java'.");
    }

    @Test
    void testCreateLoan() throws Exception {
        boolean isCreated = LoanRepository.createLoan(1, 2, LocalDate.now(), LocalDate.now().plusDays(14));
        assertTrue(isCreated, "The loan should be created successfully.");

        int count = LoanRepository.countLoanRecords();
        assertEquals(3, count, "The loan count should now be 3.");
    }

    @Test
    void testUpdateLoan() throws Exception {
        boolean isUpdated = LoanRepository.updateLoan(1, 1);
        assertTrue(isUpdated, "The loan should be updated successfully.");

        List<Loan> loans = LoanRepository.getLoansByMemberId(1);
        assertNotNull(loans.get(0).getReturnDate(), "The return date should be set.");
    }

    @Test
    void testGetBorrowingLoansByMemberId() throws Exception {
        List<Loan> loans = LoanRepository.getBorrowingLoansByMemberId(1);
        assertEquals(1, loans.size(), "Member 1 should have 1 borrowing loan.");
        assertEquals("Effective Java", loans.get(0).getBook().getTitle(), "The loaned book should be 'Effective Java'.");
    }

    @Test
    void testGetReturnedLoansByMemberId() throws Exception {
        List<Loan> loans = LoanRepository.getReturnedLoansByMemberId(2);
        assertEquals(1, loans.size(), "Member 2 should have 1 returned loan.");
        assertEquals("Clean Code", loans.get(0).getBook().getTitle(), "The returned book should be 'Clean Code'.");
    }

    @Test
    void testCheckBookQuantity() throws Exception {
        Book book = new Book();
        book.setISBN("1234567890");

        int quantity = LoanRepository.checkBookQuantity(book);
        assertEquals(5, quantity, "The quantity of 'Effective Java' should be 5.");
    }

    @Test
    void testUpdateQuantityAfterBorrow() throws Exception {
        Book book = new Book();
        book.setISBN("1234567890");

        LoanRepository.updateQuantityAfterBorrow(book);

        int quantity = LoanRepository.checkBookQuantity(book);
        assertEquals(4, quantity, "The quantity of 'Effective Java' should decrease to 4 after borrowing.");
    }

    @Test
    void testUpdateQuantityAfterReturn() throws Exception {
        Book book = new Book();
        book.setISBN("1234567890");

        LoanRepository.updateQuantityAfterReturn(book);

        int quantity = LoanRepository.checkBookQuantity(book);
        assertEquals(6, quantity, "The quantity of 'Effective Java' should increase to 6 after returning.");
    }
}