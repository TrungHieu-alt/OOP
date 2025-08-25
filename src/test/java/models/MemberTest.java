package models;

import exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import services.BookService;
import services.LoanService;
import services.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberTest {

    private Member member;
    private BookService mockBookService;
    private LoanService mockLoanService;
    private UserService mockUserService;

    @BeforeEach
    void setUp() {
        // Mock the services
        mockBookService = Mockito.mock(BookService.class);
        mockLoanService = Mockito.mock(LoanService.class);
        mockUserService = Mockito.mock(UserService.class);

        // Create a Member instance with mocked services
        member = new Member(1, "John", "Doe");
        member.bookService = mockBookService;
        member.loanService = mockLoanService;
        member.userService = mockUserService;
    }

    @Test
    void testBorrowBook_Success() throws Exception {
        // Arrange
        Book book = new Book();
        book.setId(1);
        book.setTitle("Effective Java");

        LocalDate dueDate = LocalDate.now().plusDays(14);

        when(mockLoanService.isBookAvailable(book)).thenReturn(true);
        when(mockLoanService.createNewLoan(any(Loan.class))).thenReturn(true);

        // Act
        boolean result = member.borrowBook(book, dueDate);

        // Assert
        assertTrue(result, "The book should be borrowed successfully.");
        verify(mockLoanService).isBookAvailable(book);
        verify(mockLoanService).createNewLoan(any(Loan.class));
        verify(mockLoanService).updateBookQuantityAfterBorrow(book);
        assertTrue(member.getBorrowingBooks().contains(book), "The book should be added to the borrowing books list.");
    }

    @Test
    void testBorrowBook_BookUnavailable() {
        // Arrange
        Book book = new Book();
        book.setId(1);
        book.setTitle("Effective Java");

        when(mockLoanService.isBookAvailable(book)).thenReturn(false);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> member.borrowBook(book, LocalDate.now().plusDays(14)));
        assertEquals("This book is currently unavailable", exception.getMessage());
        verify(mockLoanService).isBookAvailable(book);
    }

    @Test
    void testBorrowBook_AlreadyLoaned() {
        // Arrange
        Book book = new Book();
        book.setId(1);
        book.setTitle("Effective Java");

        member.getBorrowingBooks().add(book);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> member.borrowBook(book, LocalDate.now().plusDays(14)));
        assertEquals("You haven't returned this book yet, please return before borrowing again", exception.getMessage());
    }

    @Test
    void testReturnBook_Success() throws Exception {
        // Arrange
        Book book = new Book();
        book.setId(1);
        book.setTitle("Effective Java");

        member.getBorrowingBooks().add(book);

        when(mockLoanService.updateLoanAfterReturned(member.getId(), book.getId())).thenReturn(true);

        // Act
        boolean result = member.returnBook(book);

        // Assert
        assertTrue(result, "The book should be returned successfully.");
        verify(mockLoanService).updateLoanAfterReturned(member.getId(), book.getId());
        verify(mockLoanService).updateBookQuantityAfterReturn(book);
        assertFalse(member.getBorrowingBooks().contains(book), "The book should be removed from the borrowing books list.");
        assertTrue(member.getReturnedBooks().contains(book), "The book should be added to the returned books list.");
    }

    @Test
    void testReturnBook_NotBorrowed() {
        // Arrange
        Book book = new Book();
        book.setId(1);
        book.setTitle("Effective Java");

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> member.returnBook(book));
        assertEquals("You are not borrowing this book.", exception.getMessage());
    }

    @Test
    void testSetBorrowingBooks_Success() throws DatabaseException {
        // Arrange
        List<Book> mockBooks = new ArrayList<>();
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Effective Java");
        mockBooks.add(book1);

        when(mockBookService.getBorrowingBooksByMemberId(member.getId())).thenReturn(mockBooks);

        // Act
        boolean result = member.setBorrowingBooks();

        // Assert
        assertTrue(result, "The borrowing books should be set successfully.");
        assertEquals(1, member.getBorrowingBooks().size(), "The borrowing books list should contain 1 book.");
        assertEquals("Effective Java", member.getBorrowingBooks().get(0).getTitle(), "The book title should match.");
        verify(mockBookService).getBorrowingBooksByMemberId(member.getId());
    }

    @Test
    void testSetReturnedBooks_Success() throws DatabaseException {
        // Arrange
        List<Book> mockBooks = new ArrayList<>();
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Effective Java");
        mockBooks.add(book1);

        when(mockBookService.getReturnedBooksByMemberId(member.getId())).thenReturn(mockBooks);

        // Act
        boolean result = member.setReturnedBooks();

        // Assert
        assertTrue(result, "The returned books should be set successfully.");
        assertEquals(1, member.getReturnedBooks().size(), "The returned books list should contain 1 book.");
        assertEquals("Effective Java", member.getReturnedBooks().get(0).getTitle(), "The book title should match.");
        verify(mockBookService).getReturnedBooksByMemberId(member.getId());
    }
}