package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        // Initialize a Book object before each test
        book = new Book(1, "Effective Java");
        book.setISBN("1234567890");
        book.setAuthor("Joshua Bloch");
        book.setPublishedDate("2018-01-06");
        book.setPublisher("Addison-Wesley");
        book.setPageCount(416);
        book.setCategories("Programming");
        book.setDescription("A comprehensive guide to best practices in Java.");
        book.setThumbnailLink("http://example.com/effective_java.jpg");
        book.setQuantity(10);
    }

    @Test
    void testDefaultConstructor() {
        Book defaultBook = new Book();
        assertNotNull(defaultBook, "The default constructor should create a non-null Book object.");
    }

    @Test
    void testParameterizedConstructor() {
        Book parameterizedBook = new Book("Clean Code", "0987654321", "Robert C. Martin", "2008-08-01", "Prentice Hall", 464, "Programming", "A guide to writing clean and maintainable code.", "http://example.com/clean_code.jpg");
        assertEquals("Clean Code", parameterizedBook.getTitle(), "The title should be 'Clean Code'.");
        assertEquals("0987654321", parameterizedBook.getISBN(), "The ISBN should be '0987654321'.");
        assertEquals("Robert C. Martin", parameterizedBook.getAuthor(), "The author should be 'Robert C. Martin'.");
        assertEquals("2008-08-01", parameterizedBook.getPublishedDate(), "The published date should be '2008-08-01'.");
        assertEquals("Prentice Hall", parameterizedBook.getPublisher(), "The publisher should be 'Prentice Hall'.");
        assertEquals(464, parameterizedBook.getPageCount(), "The page count should be 464.");
        assertEquals("Programming", parameterizedBook.getCategories(), "The categories should be 'Programming'.");
        assertEquals("A guide to writing clean and maintainable code.", parameterizedBook.getDescription(), "The description should match.");
        assertEquals("http://example.com/clean_code.jpg", parameterizedBook.getThumbnailLink(), "The thumbnail link should match.");
        assertEquals(10, parameterizedBook.getQuantity(), "The default quantity should be 20.");
    }

    @Test
    void testGetAndSetId() {
        assertEquals(1, book.getId(), "The ID should be 1.");
        book.setId(2);
        assertEquals(2, book.getId(), "The ID should be updated to 2.");
    }

    @Test
    void testGetAndSetTitle() {
        assertEquals("Effective Java", book.getTitle(), "The title should be 'Effective Java'.");
        book.setTitle("Clean Code");
        assertEquals("Clean Code", book.getTitle(), "The title should be updated to 'Clean Code'.");
    }

    @Test
    void testGetAndSetISBN() {
        assertEquals("1234567890", book.getISBN(), "The ISBN should be '1234567890'.");
        book.setISBN("0987654321");
        assertEquals("0987654321", book.getISBN(), "The ISBN should be updated to '0987654321'.");
    }

    @Test
    void testGetAndSetAuthor() {
        assertEquals("Joshua Bloch", book.getAuthor(), "The author should be 'Joshua Bloch'.");
        book.setAuthor("Robert C. Martin");
        assertEquals("Robert C. Martin", book.getAuthor(), "The author should be updated to 'Robert C. Martin'.");
    }

    @Test
    void testGetAndSetPublishedDate() {
        assertEquals("2018-01-06", book.getPublishedDate(), "The published date should be '2018-01-06'.");
        book.setPublishedDate("2008-08-01");
        assertEquals("2008-08-01", book.getPublishedDate(), "The published date should be updated to '2008-08-01'.");
    }

    @Test
    void testGetAndSetPublisher() {
        assertEquals("Addison-Wesley", book.getPublisher(), "The publisher should be 'Addison-Wesley'.");
        book.setPublisher("Prentice Hall");
        assertEquals("Prentice Hall", book.getPublisher(), "The publisher should be updated to 'Prentice Hall'.");
    }

    @Test
    void testGetAndSetPageCount() {
        assertEquals(416, book.getPageCount(), "The page count should be 416.");
        book.setPageCount(464);
        assertEquals(464, book.getPageCount(), "The page count should be updated to 464.");
    }

    @Test
    void testGetAndSetCategories() {
        assertEquals("Programming", book.getCategories(), "The categories should be 'Programming'.");
        book.setCategories("Software Development");
        assertEquals("Software Development", book.getCategories(), "The categories should be updated to 'Software Development'.");
    }

    @Test
    void testGetAndSetDescription() {
        assertEquals("A comprehensive guide to best practices in Java.", book.getDescription(), "The description should match.");
        book.setDescription("A guide to writing clean and maintainable code.");
        assertEquals("A guide to writing clean and maintainable code.", book.getDescription(), "The description should be updated.");
    }

    @Test
    void testGetAndSetThumbnailLink() {
        assertEquals("http://example.com/effective_java.jpg", book.getThumbnailLink(), "The thumbnail link should match.");
        book.setThumbnailLink("http://example.com/clean_code.jpg");
        assertEquals("http://example.com/clean_code.jpg", book.getThumbnailLink(), "The thumbnail link should be updated.");
    }

    @Test
    void testGetAndSetQuantity() {
        assertEquals(10, book.getQuantity(), "The quantity should be 10.");
        book.setQuantity(15);
        assertEquals(15, book.getQuantity(), "The quantity should be updated to 15.");
    }
}