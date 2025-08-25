package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanTest {

    private Member member;
    private Book book;
    private Loan loan;

    @BeforeEach
    void setUp() {
        // Create mock Member and Book objects
        member = new Member();
        member.setId(1);
        member.setName("John Doe");

        book = new Book();
        book.setTitle("Effective Java");
        book.setISBN("1234567890");

        // Create a Loan object
        loan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(14));
    }

    @Test
    void testConstructorWithAllParameters() {
        assertEquals(member, loan.getMember(), "Member should be set correctly.");
        assertEquals(book, loan.getBook(), "Book should be set correctly.");
        assertEquals(LocalDate.now(), loan.getIssueDate(), "Issue date should be set to today.");
        assertEquals(LocalDate.now().plusDays(14), loan.getDueDate(), "Due date should be 14 days from today.");
        assertNull(loan.getReturnDate(), "Return date should be null by default.");
    }

    @Test
    void testConstructorWithDefaultIssueDate() {
        Loan loanWithDefaultIssueDate = new Loan(member, book, LocalDate.now().plusDays(7));
        assertEquals(LocalDate.now(), loanWithDefaultIssueDate.getIssueDate(), "Issue date should default to today.");
        assertEquals(LocalDate.now().plusDays(7), loanWithDefaultIssueDate.getDueDate(), "Due date should be 7 days from today.");
    }

    @Test
    void testSetAndGetLoanId() {
        loan.setLoanId("L001");
        assertEquals("L001", loan.getLoanId(), "Loan ID should be set and retrieved correctly.");
    }

    @Test
    void testSetAndGetReturnDate() {
        LocalDate returnDate = LocalDate.now().plusDays(10);
        loan.setReturnDate(returnDate);
        assertEquals(returnDate, loan.getReturnDate(), "Return date should be set and retrieved correctly.");
    }

    @Test
    void testIsReturned() {
        assertFalse(loan.isReturned(), "Loan should not be marked as returned initially.");
        loan.setReturnDate(LocalDate.now());
        assertTrue(loan.isReturned(), "Loan should be marked as returned after setting a return date.");
    }

    @Test
    void testIsOverdueWithoutReturn() {
        Loan overdueLoan = new Loan(member, book, LocalDate.now().minusDays(7), LocalDate.now().minusDays(1));
        assertTrue(overdueLoan.isOverdue(), "Loan should be overdue if the due date has passed and it is not returned.");
    }

    @Test
    void testIsOverdueWithReturn() {
        Loan returnedLoan = new Loan(member, book, LocalDate.now().minusDays(7), LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now().plusDays(1)); // Returned late
        assertTrue(returnedLoan.isOverdue(), "Loan should be overdue if it was returned after the due date.");

        returnedLoan.setReturnDate(LocalDate.now().minusDays(2)); // Returned on time
        assertFalse(returnedLoan.isOverdue(), "Loan should not be overdue if it was returned on or before the due date.");
    }

    @Test
    void testSetAndGetMember() {
        Member newMember = new Member();
        newMember.setId(2);
        newMember.setName("Jane Doe");
        loan.setMember(newMember);
        assertEquals(newMember, loan.getMember(), "Member should be set and retrieved correctly.");
    }

    @Test
    void testSetAndGetBook() {
        Book newBook = new Book();
        newBook.setTitle("Clean Code");
        newBook.setISBN("0987654321");
        loan.setBook(newBook);
        assertEquals(newBook, loan.getBook(), "Book should be set and retrieved correctly.");
    }
}