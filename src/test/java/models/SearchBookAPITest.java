package models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchBookAPITest {

    private SearchBookAPI searchBookAPI;

    @BeforeEach
    void setUp() {
        searchBookAPI = new SearchBookAPI();
    }

    @Test
    void testGetBookInfos_withValidResponse() throws IOException {
        // Mock a valid API response
        String mockResponse = """
        {
            "items": [
                {
                    "volumeInfo": {
                        "title": "Effective Java",
                        "industryIdentifiers": [
                            {"type": "ISBN_10", "identifier": "0134685997"},
                            {"type": "ISBN_13", "identifier": "9780134685991"}
                        ],
                        "authors": ["Joshua Bloch"],
                        "publishedDate": "2018-01-06",
                        "publisher": "Addison-Wesley",
                        "pageCount": 416,
                        "categories": ["Programming"],
                        "description": "A comprehensive guide to best practices in Java.",
                        "imageLinks": {
                            "thumbnail": "http://example.com/effective_java.jpg"
                        }
                    }
                }
            ]
        }
        """;

        // Use ObjectMapper to simulate the API response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(mockResponse);

        // Simulate the API call
        searchBookAPI.searchResult.clear(); // Clear previous results
        searchBookAPI.getBookInfos("Effective Java");

        // Verify the results
        List<Book> results = SearchBookAPI.searchResult;
        assertEquals(1, results.size());

        Book book = results.get(0);
        assertEquals("Effective Java", book.getTitle());
        assertEquals("9780134685991", book.getIsbn());
        assertEquals("Joshua Bloch", book.getAuthors());
        assertEquals("2018-01-06", book.getPublishedDate());
        assertEquals("Addison-Wesley", book.getPublisher());
        assertEquals(416, book.getPageCount());
        assertEquals("Programming", book.getCategories());
        assertEquals("http://example.com/effective_java.jpg", book.getThumbnailLink());
    }

    @Test
    void testGetBookInfos_withEmptyResponse() throws IOException {
        // Mock an empty API response
        String mockResponse = """
        {
            "items": []
        }
        """;

        // Simulate the API call
        searchBookAPI.searchResult.clear(); // Clear previous results
        searchBookAPI.getBookInfos("Nonexistent Book");

        // Verify the results
        List<Book> results = SearchBookAPI.searchResult;
        assertTrue(results.isEmpty());
    }
}