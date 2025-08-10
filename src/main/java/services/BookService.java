package services;

import exceptions.DatabaseException;
import exceptions.DuplicateDataException;
import exceptions.InvalidDataException;
import models.Book;
import repository.BookRepository;

import java.util.List;

public class BookService {

    private void validateBook(Book book) throws InvalidDataException {
        if (book == null) {
            throw new InvalidDataException("book", "Book must not be null");
        }
        if (book.getISBN() == null || book.getISBN().isBlank()) {
            throw new InvalidDataException("isbn", "ISBN must not be blank");
        }
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new InvalidDataException("title", "Title must not be blank");
        }
        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            throw new InvalidDataException("author", "Author must not be blank");
        }
        if (book.getQuantity() < 0) {
            throw new InvalidDataException("quantity", "Quantity must be >= 0");
        }
    }

    public boolean addBook(Book book)
            throws DatabaseException, DuplicateDataException, InvalidDataException {

        validateBook(book);

        // Trùng dữ liệu -> DuplicateDataException
        if (BookRepository.doesBookExists(book.getISBN())) {
            throw new DuplicateDataException("isbn", book.getISBN(),
                    "Book already exists with this ISBN");
        }

        // Hợp lệ -> thêm
        return BookRepository.addBook(book);
    }

    public boolean updateBook(Book book)
            throws DatabaseException, InvalidDataException {

        validateBook(book);

        // Không tìm thấy -> InvalidDataException
        if (!BookRepository.doesBookExists(book.getISBN())) {
            throw new InvalidDataException("isbn", "Book not found with this ISBN");
        }

        return BookRepository.updateBook(book);
    }

    public boolean removeBook(Book book)
            throws DatabaseException, InvalidDataException {

        if (book == null || book.getISBN() == null || book.getISBN().isBlank()) {
            throw new InvalidDataException("isbn", "ISBN must not be blank");
        }
        if (!BookRepository.doesBookExists(book.getISBN())) {
            throw new InvalidDataException("isbn", "Book not found with this ISBN");
        }

        return BookRepository.removeBook(book);
    }

    public boolean doesBookExist(String isbn)
            throws DatabaseException, InvalidDataException {

        if (isbn == null || isbn.isBlank()) {
            throw new InvalidDataException("isbn", "ISBN must not be blank");
        }
        return BookRepository.doesBookExists(isbn);
    }

    public List<Book> getMostPopularBooks() throws DatabaseException {
        return BookRepository.getMostPopularBooks();
    }

    public List<Book> getNewBooks() throws DatabaseException {
        return BookRepository.getNewBooks();
    }

    public List<Book> getAllBooks() throws DatabaseException {
        return BookRepository.getAllBooks();
    }

    public List<Book> searchBooks(String queryText) throws DatabaseException {
        return BookRepository.searchBooks(queryText == null ? "" : queryText.trim());
    }

    public int getBookIdByISBN(Book book)
            throws DatabaseException, InvalidDataException {
        if (book == null || book.getISBN() == null || book.getISBN().isBlank()) {
            throw new InvalidDataException("isbn", "ISBN must not be blank");
        }
        return BookRepository.getBookIdByISBN(book);
    }

    public List<Book> getBorrowingBooksByMemberId(int id) throws DatabaseException {
        return BookRepository.getBorrowingBooksByMemberId(id);
    }

    public List<Book> getReturnedBooksByMemberId(int id) throws DatabaseException {
        return BookRepository.getReturnedBooksByMemberId(id);
    }

    public int getBookQuantity(Book book)
            throws DatabaseException, InvalidDataException {
        if (book == null || book.getISBN() == null || book.getISBN().isBlank()) {
            throw new InvalidDataException("isbn", "ISBN must not be blank");
        }
        return BookRepository.getBookQuantity(book);
    }

    public int countBookRecords() throws DatabaseException {
        return BookRepository.countBookRecords();
    }
}
