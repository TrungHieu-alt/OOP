package repository;

import exceptions.DatabaseException;
import models.Book;

import java.sql.*;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    // ---------- Helpers ----------
    private static Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setPublishedDate(rs.getString("published_date"));
        b.setPublisher(rs.getString("publisher"));
        b.setPageCount(rs.getInt("page_count"));
        b.setCategories(rs.getString("categories"));
        b.setDescription(rs.getString("description"));
        b.setThumbnailLink(rs.getString("thumbnail_link"));
        b.setISBN(rs.getString("isbn"));
        b.setQuantity(rs.getInt("quantity"));
        return b;
    }

    /** Chuẩn hoá published_date từ yyyy | yyyy-MM | yyyy-MM-dd -> yyyy-MM-dd. Null/blank -> "2000-01-01". */
    private static String normalizePublishedDate(String input) {
        if (input == null || input.isBlank()) return "2000-01-01";
        try {
            if (input.length() == 4) {
                return Year.parse(input).atMonth(1).atDay(1).toString();
            } else if (input.length() == 7) {
                return YearMonth.parse(input).atDay(1).toString();
            } else {
                return LocalDate.parse(input).toString();
            }
        } catch (DateTimeParseException e) {

            return "2000-01-01";
        }
    }

    // ---------- Queries ----------
    public static int countBookRecords() throws DatabaseException {
        String sql = "SELECT COUNT(*) AS total FROM books";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error counting book records", e);
        }
    }

    public static List<Book> getAllBooks() throws DatabaseException {
        String sql = "SELECT * FROM books";
        List<Book> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapRow(rs));
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all books", e);
        }
    }

    public static List<Book> getMostPopularBooks() throws DatabaseException {
        String sql =
                "SELECT b.id, b.title, b.author, b.published_date, b.publisher, b.page_count, " +
                        "       b.categories, b.description, b.thumbnail_link, b.isbn, b.quantity, " +
                        "       COUNT(*) AS borrow_count " +
                        "FROM loans l JOIN books b ON b.id = l.book_id " +
                        "GROUP BY b.id, b.title, b.author, b.published_date, b.publisher, b.page_count, " +
                        "         b.categories, b.description, b.thumbnail_link, b.isbn, b.quantity " +
                        "ORDER BY borrow_count DESC " +
                        "LIMIT 5";

        List<Book> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapRow(rs));
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching most popular books", e);
        }
    }

    public static List<Book> getNewBooks() throws DatabaseException {
        String sql = "SELECT * FROM books ORDER BY id DESC LIMIT 10";
        List<Book> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapRow(rs));
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching newest books", e);
        }
    }

    // ---------- Commands ----------
    public static boolean addBook(Book book) throws DatabaseException {
        String sql = "INSERT INTO books " +
                "(title, author, isbn, published_date, publisher, page_count, categories, description, thumbnail_link, quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getISBN());
            ps.setString(4, normalizePublishedDate(book.getPublishedDate()));
            ps.setString(5, book.getPublisher());
            // Nếu pageCount có thể null/0, setObject để chèn NULL
            if (book.getPageCount() <= 0) ps.setObject(6, null, Types.INTEGER);
            else ps.setLong(6, book.getPageCount());
            ps.setString(7, book.getCategories());
            ps.setString(8, book.getDescription());
            ps.setString(9, book.getThumbnailLink());
            ps.setInt(10, book.getQuantity());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding book", e);
        }
    }

    public static boolean removeBook(Book book) throws DatabaseException {
        String sql = "DELETE FROM books WHERE isbn = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, book.getISBN());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error removing book", e);
        }
    }

    public static boolean updateBook(Book book) throws DatabaseException {
        String sql = "UPDATE books SET title=?, author=?, published_date=?, publisher=?, " +
                "description=?, categories=?, quantity=? WHERE isbn=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, normalizePublishedDate(book.getPublishedDate()));
            ps.setString(4, book.getPublisher());
            ps.setString(5, book.getDescription());
            ps.setString(6, book.getCategories());
            ps.setInt(7, book.getQuantity());
            ps.setString(8, book.getISBN());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating book information", e);
        }
    }

    public static List<Book> searchBooks(String queryText) throws DatabaseException {
        String q = (queryText == null) ? "" : queryText.trim().toLowerCase();
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ?";

        List<Book> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + q + "%");
            ps.setString(2, "%" + q + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Error searching for books", e);
        }
    }

    public static int getBookIdByISBN(Book book) throws DatabaseException {
        String sql = "SELECT id FROM books WHERE isbn = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, book.getISBN());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching book ID by ISBN", e);
        }
    }

    public static List<Book> getBorrowingBooksByMemberId(int memberId) throws DatabaseException {
        String sql = "SELECT b.id, b.title FROM loans l JOIN books b ON l.book_id = b.id " +
                "WHERE l.member_id = ? AND l.return_date IS NULL";
        List<Book> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getInt("id"));
                    b.setTitle(rs.getString("title"));
                    result.add(b);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching borrowing books for member ID: " + memberId, e);
        }
    }

    public static List<Book> getReturnedBooksByMemberId(int memberId) throws DatabaseException {
        String sql = "SELECT b.id, b.title FROM loans l JOIN books b ON l.book_id = b.id " +
                "WHERE l.member_id = ? AND l.return_date IS NOT NULL";
        List<Book> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getInt("id"));
                    b.setTitle(rs.getString("title"));
                    result.add(b);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching returned books for member ID: " + memberId, e);
        }
    }

    public static boolean doesBookExists(String isbn) throws DatabaseException {
        String sql = "SELECT 1 FROM books WHERE isbn = ? LIMIT 1";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking existence of book by ISBN", e);
        }
    }

    public static int getBookQuantity(Book book) throws DatabaseException {
        String sql = "SELECT quantity FROM books WHERE isbn = ? LIMIT 1";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, book.getISBN());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("quantity") : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching quantity for book by ISBN", e);
        }
    }
}
