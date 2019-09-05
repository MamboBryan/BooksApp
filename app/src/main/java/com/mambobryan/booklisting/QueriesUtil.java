package com.mambobryan.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueriesUtil {

    /**
     * Create a private constructor because no one should ever create a {@link QueriesUtil} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueriesUtil (and an object instance of QueriesUtil is not needed).
     */
    private QueriesUtil() {
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueriesUtil.class.getSimpleName();

    public static List<Book> fetchBookData(String requestUrl) {

        Log.i(LOG_TAG, "Connecting to Google server and getting data");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Book} object by parsing out information
     * about the first earthquake from the input booksJSON string.
     */
    private static List<Book> extractFeatureFromJson(String booksJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(booksJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of books.
            JSONArray items = baseJsonResponse.getJSONArray("items");

            // If there are results in the items array
            for (int i = 0; i < items.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject bookData = items.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all information
                // for that book.
                JSONObject bookInfo = bookData.getJSONObject("volumeInfo");

                // Extract the value for the key called "title"
                String bookTitle = bookInfo.getString("title");
                Log.i(LOG_TAG, "Title has been found");

                // Extract the value for the key called author
                String bookAuthor;
                if (bookInfo.has("authors")) {
                    JSONArray authors = bookInfo.getJSONArray("authors");
                    Log.i(LOG_TAG, "Author array has been found");

                    //Array authorsArray = loadJSONArray(authors);
                    bookAuthor = authors.getString(0);
                } else {
                    bookAuthor = "No author";
                }

                //Extract the first key that we will use for the link
                String bookLink = bookData.getString("selfLink");

                // Extract the value for the key called "time"
                Double bookRating;
                if (bookInfo.has("averageRating")) {

                    bookRating = bookInfo.getDouble("averageRating");

                } else {
                    bookRating = 0.0;
                }

                // Extract the value for the key called "publisher"
                String bookPublisher;
                if (bookInfo.has("publisher")) {

                    bookPublisher = bookInfo.getString("publisher");

                } else {
                    bookPublisher = "Undisclosed publisher";
                }

                //Extract the value for the key called "publishedDate"
                String bookPublishedDate;
                if (bookInfo.has("publishedDate")) {

                    bookPublishedDate = bookInfo.getString("publishedDate");

                } else {
                    bookPublishedDate = "No published date";
                }

                //Extract the value for the key called "previewLink"
                String bookPreviewLink = bookInfo.getString("previewLink");

                //Extract the value for the key called "imageLinks"
                JSONObject bookLinks = bookInfo.getJSONObject("imageLinks");
                String bookImageLink = bookLinks.getString("thumbnail");

                //Extract the value for the key called "description"
                String bookDescription;
                if (bookInfo.has("description")) {

                    bookDescription = bookInfo.getString("description");

                } else {
                    bookDescription = "No book description";
                }

                //Extract the value fir the key called "pageCount"
                int bookPages;
                if (bookInfo.has("pageCount")) {

                    bookPages = bookInfo.getInt("pageCount");

                } else {

                    bookPages = 0;
                }

                // Create a new {@link Book} object with the params
                // from the JSON response.
                Book newBook = new Book(bookAuthor, bookTitle, bookRating, bookLink, bookPublisher,
                        bookPublishedDate, bookPreviewLink, bookImageLink, bookDescription, bookPages);

                // Add the new {@link Earthquake} to the list of books.
                books.add(newBook);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the Book JSON results", e);
        }
        return books;
    }
}
