package controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Book;
import repository.BookRepository;
import services.BookService;
import services.UserSession;
import ui_helper.CardHelper;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for managing the dashboard view for members.
 */
public class DashboardMemberController implements Initializable {

    @FXML
    private AnchorPane dashboard_anchorpane;

    @FXML
    private HBox mostpopular_HBox;

    @FXML
    private ScrollPane mostpopular_scrollpane;

    @FXML
    private HBox newbooks_HBox;

    @FXML
    private ScrollPane newbooks_scrollpane;

    @FXML private HBox recommended_HBox;
    @FXML private ScrollPane recommended_scrollpane;

    private BookService bookService;
    private String username;
    private static final BookRepository bookRepository = new BookRepository();

    /**
     * Initializes the controller class.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bookService = new BookService(bookRepository);

        this.username = UserSession.get().getUsername();
        System.out.println("[DashboardMemberController] session username=" + this.username);

        displayMostPopularBooks();
        displayNewBooks();

        if (this.username != null && !this.username.isEmpty()) {
            displayRecommendedBooks();
        } else {
            System.out.println("[DashboardMemberController] username is null/empty -> skip recommend.");
            Label warn = new Label("No username found in session.");
            recommended_HBox.getChildren().setAll(warn);
        }
    }


    /**
     * Displays the most popular books in the dashboard.
     */
    public void displayMostPopularBooks() {
        mostpopular_HBox.getChildren().clear();
        Label loadingLabel = new Label("Loading most popular books...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        mostpopular_HBox.getChildren().add(loadingLabel);

        Task<List<HBox>> loadMostPopularBooksTask = new Task<>() {
            @Override
            protected List<HBox> call() throws Exception {
                List<HBox> popularBooks = new ArrayList<>();
                List<Book> books = bookService.getMostPopularBooks();

                for (Book book : books) {
                    HBox bigCard_box = CardHelper.displayBigCard(book);
                    popularBooks.add(bigCard_box);
                }
                return popularBooks;
            }
        };

        loadMostPopularBooksTask.setOnSucceeded(event -> {
            mostpopular_HBox.getChildren().clear();
            List<HBox> popularBooks = loadMostPopularBooksTask.getValue();
            mostpopular_HBox.getChildren().addAll(popularBooks);
        });

        loadMostPopularBooksTask.setOnFailed(event -> {
            mostpopular_HBox.getChildren().clear();
            Label errorLabel = new Label("Failed to load most popular books.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            mostpopular_HBox.getChildren().add(errorLabel);
        });

        Thread thread = new Thread(loadMostPopularBooksTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Displays the new books in the dashboard.
     */
    public void displayNewBooks() {
        newbooks_HBox.getChildren().clear();
        Label loadingLabel = new Label("Loading new books...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        newbooks_HBox.getChildren().add(loadingLabel);

        // Tạo background task
        Task<List<VBox>> loadNewBooksTask = new Task<>() {
            @Override
            protected List<VBox> call() throws Exception {
                List<VBox> newBooks = new ArrayList<>();
                List<Book> books = bookService.getNewBooks();

                for (Book book : books) {
                    VBox smallCard_box = CardHelper.displaySmallCard(book);
                    newBooks.add(smallCard_box);
                }
                return newBooks;
            }
        };

        loadNewBooksTask.setOnSucceeded(event -> {
            newbooks_HBox.getChildren().clear();
            List<VBox> newBooks = loadNewBooksTask.getValue();
            newbooks_HBox.getChildren().addAll(newBooks);
        });

        loadNewBooksTask.setOnFailed(event -> {
            newbooks_HBox.getChildren().clear();
            Label errorLabel = new Label("Failed to load new books.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            newbooks_HBox.getChildren().add(errorLabel);
        });

        Thread thread = new Thread(loadNewBooksTask);
        thread.setDaemon(true);
        thread.start();
    }

    public void displayRecommendedBooks() {
        final String u = (this.username != null) ? this.username : UserSession.get().getUsername();

        recommended_HBox.getChildren().clear();
        Label loadingLabel = new Label("Loading recommended books...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        recommended_HBox.getChildren().add(loadingLabel);

        Task<List<HBox>> loadRecommendedBooksTask = new Task<>() {
            @Override
            protected List<HBox> call() throws Exception {
                List<HBox> cards = new ArrayList<>();
                List<Book> books = bookService.getRecommendedBooksForUser(u);
                if (books == null || books.isEmpty()) {
                    return null;
                }

                for (Book book : books) {
                    HBox bigCard = CardHelper.displayBigCard(book);
                    cards.add(bigCard);
                }
                return cards;
            }
        };

        loadRecommendedBooksTask.setOnSucceeded(e -> {
            recommended_HBox.getChildren().clear();
            List<HBox> cards = loadRecommendedBooksTask.getValue();

            if (cards == null || cards.isEmpty()) {
                Label hint = new Label("📚 No recommendations are available. Please borrow at least one book to enable personalized suggestions.");
                hint.setStyle("-fx-font-size: 16px; -fx-text-fill: gray; -fx-font-style: italic;");
                recommended_HBox.getChildren().add(hint);
            } else {
                recommended_HBox.getChildren().addAll(cards);
            }
        });

        loadRecommendedBooksTask.setOnFailed(e -> {
            recommended_HBox.getChildren().clear();
            Label errorLabel = new Label("Failed to load recommended books.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            recommended_HBox.getChildren().add(errorLabel);
        });

        Thread t = new Thread(loadRecommendedBooksTask);
        t.setDaemon(true);
        t.start();
    }
}
