package repository;

import exceptions.DatabaseException;
import models.Book;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryTest {

    private static Connection connection;

    @BeforeAll
    static void setupDatabase() throws Exception {
        // Set up an in-memory H2 database for testing
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement statement = connection.createStatement();

        // Create the books table
        statement.execute("CREATE TABLE books (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255), " +
                "author VARCHAR(255), " +
                "published_date VARCHAR(255), " +
                "categories VARCHAR(255), " +
                "description TEXT, " +
                "thumbnail_link VARCHAR(255), " +
                "isbn VARCHAR(255), " +
                "quantity INT)");

        // Insert sample data
        statement.execute("INSERT INTO books (title, author, published_date, categories, description, thumbnail_link, isbn, quantity) " +
                "VALUES ('Book 1', 'Author 1', '2023-01-01', 'Category 1', 'Description 1', 'http://example.com/thumb1.jpg', '1234567890', 10)");
        statement.execute("INSERT INTO books (title, author, published_date, categories, description, thumbnail_link, isbn, quantity) " +
                "VALUES ('Book 2', 'Author 2', '2023-01-02', 'Category 2', 'Description 2', 'http://example.com/thumb2.jpg', '0987654321', 5)");

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
    void testCountBookRecords() throws DatabaseException {
        int count = BookRepository.countBookRecords();
        assertEquals(2, count, "The book count should be 2.");
    }

    @Test
    void testGetAllBooks() throws DatabaseException {
        List<Book> books = BookRepository.getAllBooks();
        assertEquals(2, books.size(), "The number of books should be 2.");
        assertEquals("Book 1", books.get(0).getTitle(), "The first book's title should be 'Book 1'.");
        assertEquals("Book 2", books.get(1).getTitle(), "The second book's title should be 'Book 2'.");
    }

    @Test
    void testAddBook() throws DatabaseException {
        Book newBook = new Book();
        newBook.setTitle("Book 3");
        newBook.setAuthor("Author 3");
        newBook.setPublishedDate("2023-01-03");
        newBook.setCategories("Category 3");
        newBook.setDescription("Description 3");
        newBook.setThumbnailLink("http://example.com/thumb3.jpg");
        newBook.setISBN("1122334455");
        newBook.setQuantity(7);

        boolean isAdded = BookRepository.addBook(newBook);
        assertTrue(isAdded, "The book should be added successfully.");

        int count = BookRepository.countBookRecords();
        assertEquals(3, count, "The book count should now be 3.");
    }

    @Test
    void testRemoveBook() throws DatabaseException {
        Book bookToRemove = new Book();
        bookToRemove.setISBN("1234567890");

        boolean isRemoved = BookRepository.removeBook(bookToRemove);
        assertTrue(isRemoved, "The book should be removed successfully.");

        int count = BookRepository.countBookRecords();
        assertEquals(1, count, "The book count should now be 1.");
    }

    @Test
    void testSearchBooks() throws DatabaseException {
        List<Book> books = BookRepository.searchBooks("Book 1");
        assertEquals(1, books.size(), "The search should return 1 book.");
        assertEquals("Book 1", books.get(0).getTitle(), "The book's title should be 'Book 1'.");
    }

    @Test
    void testDoesBookExists() throws DatabaseException {
        boolean exists = BookRepository.doesBookExists("1234567890");
        assertTrue(exists, "The book with ISBN '1234567890' should exist.");

        boolean notExists = BookRepository.doesBookExists("0000000000");
        assertFalse(notExists, "The book with ISBN '0000000000' should not exist.");
    }
}
