package controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Book;
import models.Member;
import repository.BookRepository;
import services.BookService;
import ui_helper.CardHelper;
import ui_helper.AlertHelper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for managing the library view.
 */
public class LibraryController implements Initializable {

    @FXML
    private AnchorPane library_anchorpane;

    @FXML
    private GridPane library_gridpane;

    @FXML
    private ScrollPane library_scrollpane;

    private BookService bookService;

    private static final BookRepository bookRepository = new BookRepository();

    // Pagination state
    private List<Book> allBooks = new ArrayList<>();
    private int loadedCount = 0;
    private static final int PAGE_SIZE = 12;
    private boolean loading = false;

    // Current logged-in member (set from outside)
    private Member member;
    public void setMember(Member member) { this.member = member; }
    public Member getMember() { return member; }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bookService = new BookService(bookRepository);
        displayLibrary();
    }

    /**
     * Displays the library books in a grid layout, paginated and incremental loading.
     */
    public void displayLibrary() {
        library_gridpane.getChildren().clear();
        loadedCount = 0;
        loading = true;

        Label loadingLabel = new Label("Loading books, please wait...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        library_gridpane.add(loadingLabel, 1, 1);

        Task<List<Book>> loadLibraryTask = new Task<>() {
            @Override
            protected List<Book> call() throws Exception {
                // Only fetch books once
                return bookService.getAllBooks();
            }
        };

        loadLibraryTask.setOnSucceeded(event -> {
            library_gridpane.getChildren().clear();
            allBooks = loadLibraryTask.getValue();
            loadedCount = 0;
            loading = false;

            // Load first page
            loadNextPage();

            // Setup scroll listener for incremental loading
            library_scrollpane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
                if (!loading && newVal.doubleValue() > 0.95) { // Near bottom
                    loadNextPage();
                }
            });
        });

        loadLibraryTask.setOnFailed(event -> {
            library_gridpane.getChildren().clear();
            Label errorLabel = new Label("Failed to load books. Please try again.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            library_gridpane.add(errorLabel, 1, 1);
        });

        Thread thread = new Thread(loadLibraryTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Loads the next page of book cards and adds them to the grid.
     */
    private void loadNextPage() {
        if (loading || loadedCount >= allBooks.size()) return;
        loading = true;

        int start = loadedCount;
        int end = Math.min(loadedCount + PAGE_SIZE, allBooks.size());
        List<Book> booksToLoad = allBooks.subList(start, end);

        Task<List<HBox>> cardTask = new Task<>() {
            @Override
            protected List<HBox> call() throws IOException {
                List<HBox> cardList = new ArrayList<>();
                for (Book book : booksToLoad) {
                    HBox bigCard_box = CardHelper.displayBigCard(book);

                    // Make card clickable for borrowing
                    bigCard_box.setOnMouseClicked(event -> {
                        if (member == null) {
                            AlertHelper.showError("No Member", "Please log in to borrow books.");
                        } else {
                            openBorrowDialog(book, member);
                        }
                    });

                    // Add hover effect for better UX
                    bigCard_box.setOnMouseEntered(e -> bigCard_box.setStyle("-fx-background-color: #f0f0f0;"));
                    bigCard_box.setOnMouseExited(e -> bigCard_box.setStyle(""));

                    cardList.add(bigCard_box);
                }
                return cardList;
            }
        };

        cardTask.setOnSucceeded(event -> {
            int column = loadedCount % 3;
            int row = loadedCount / 3 + 1;
            List<HBox> cards = cardTask.getValue();

            for (HBox card : cards) {
                if (column >= 3) {
                    column = 0;
                    row++;
                }
                library_gridpane.add(card, column++, row);
                GridPane.setMargin(card, new Insets(8));
            }
            loadedCount = end;
            loading = false;
        });

        cardTask.setOnFailed(event -> {
            loading = false;
        });

        Thread thread = new Thread(cardTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Opens borrow dialog for selected book and member.
     */
    private void openBorrowDialog(Book book, Member member) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BorrowForm.fxml"));
            Parent root = loader.load();
            BorrowBookController borrowController = loader.getController();
            borrowController.setData(book, member);
            borrowController.handleBorrow(book, member, () -> {
                AlertHelper.showInformation("Book Borrowed", "You have borrowed this book successfully.");
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            });

            Stage stage = new Stage();
            stage.setTitle("Borrow Book");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(library_anchorpane.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Dialog Error", "Could not open borrow dialog.");
        }
    }
}
