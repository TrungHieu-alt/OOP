package repository;

import models.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private static final String URL = "jdbc:mysql://localhost:3306/library_management_system";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Thiết lập kết nối
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Thực thi truy vấn SELECT và ánh xạ ResultSet thành List<Book>
    private static List<Book> executeQuery(String sql, Object... params) {
        List<Book> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thực thi INSERT/UPDATE/DELETE
    private static boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Ánh xạ 1 dòng ResultSet thành 1 đối tượng Book
    private static Book mapRow(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublishedDate(rs.getString("published_date"));
        book.setCategories(rs.getString("categories"));
        book.setDescription(rs.getString("description"));
        book.setThumbnailLink(rs.getString("thumbnail_link"));
        book.setISBN(rs.getString("isbn"));
        book.setQuantity(rs.getInt("quantity"));
        return book;
    }

    // Chuẩn hóa publishedDate: nếu chỉ nhập năm hoặc năm-tháng
    private static String normalizeDate(String date) {
        if (date.matches("\\d{4}")) {
            return date + "-01-01";
        } else if (date.matches("\\d{4}-\\d{2}")) {
            return date + "-01";
        }
        return date;
    }

    public static int countBooks() {
        String sql = "SELECT COUNT(*) AS total FROM books";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Book> getAllBooks() {
        return executeQuery("SELECT * FROM books");
    }

    public static List<Book> getMostPopularBooks() {
        String sql = "SELECT b.*, COUNT(l.book_id) AS borrow_count " +
                "FROM books b JOIN loans l ON b.id = l.book_id " +
                "GROUP BY b.id ORDER BY borrow_count DESC LIMIT 5";
        return executeQuery(sql);
    }

    public static List<Book> getNewBooks() {
        String sql = "SELECT * FROM books ORDER BY id DESC LIMIT 10";
        return executeQuery(sql);
    }

    public static boolean addBook(Book book) {
        String sql = "INSERT INTO books " +
                "(title, author, isbn, published_date, publisher, page_count, categories, description, thumbnail_link, quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql,
                book.getTitle(),
                book.getAuthor(),
                book.getISBN(),
                normalizeDate(book.getPublishedDate()),
                book.getPublisher(),
                book.getPageCount(),
                book.getCategories(),
                book.getDescription(),
                book.getThumbnailLink(),
                book.getQuantity()
        );
    }

    public static boolean removeBook(String isbn) {
        return executeUpdate("DELETE FROM books WHERE isbn = ?", isbn);
    }

    public static boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, published_date = ?, publisher = ?, description = ?, categories = ?, quantity = ? " +
                "WHERE isbn = ?";
        return executeUpdate(sql,
                book.getTitle(),
                book.getAuthor(),
                normalizeDate(book.getPublishedDate()),
                book.getPublisher(),
                book.getDescription(),
                book.getCategories(),
                book.getQuantity(),
                book.getISBN()
        );
    }

    public static List<Book> searchBooks(String queryText) {
        String pattern = "%" + queryText + "%";
        return executeQuery("SELECT * FROM books WHERE title LIKE ? OR author LIKE ?", pattern, pattern);
    }

    public static int getBookIdByISBN(String isbn) {
        List<Book> list = executeQuery("SELECT id FROM books WHERE isbn = ?", isbn);
        return list.isEmpty() ? -1 : list.get(0).getId();
    }
}
