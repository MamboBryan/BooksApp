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

public class DetailQueryUtil {

    public DetailQueryUtil() {
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = DetailQueryUtil.class.getSimpleName();

    public static Book fetchBookData(String requestUrl) {

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
        Book bookDetail = extractBookDetailFromJSON(jsonResponse);

        // Return the {@link Event}
        return bookDetail;
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
    private static Book extractBookDetailFromJSON(String booksJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        Book bookDetails = null;

        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(booksJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of books.
            JSONObject detailedBookInfo = baseJsonResponse.getJSONObject("volumeInfo");

            // Extract the value for the key called "title"
            String bookTitle = detailedBookInfo.getString("title");
            Log.i(LOG_TAG, "Title has been found");

            // Extract the value for the key called author
            String bookAuthor;
            if (detailedBookInfo.has("authors")){
                JSONArray authors = detailedBookInfo.getJSONArray("authors");
                Log.i(LOG_TAG, "Author array has been found");

                //Array authorsArray = loadJSONArray(authors);
                bookAuthor = authors.getString(0);
            } else {
                bookAuthor = "No author";
            }

            //Extract the first key that we will use for the link
            String bookLink = baseJsonResponse.getString("selfLink");

            // Extract the value for the key called "time"
            Double bookRating ;
            if (detailedBookInfo.has("averageRating")){

                bookRating = detailedBookInfo.getDouble("averageRating");

            } else {
                bookRating = 0.0;
            }

            // Extract the value for the key called "publisher"
            String bookPublisher;
            if (detailedBookInfo.has("publisher")){

                bookPublisher = detailedBookInfo.getString("publisher");

            } else {
                bookPublisher = "Undisclosed publisher";
            }

            //Extract the value for the key called "publishedDate"
            String bookPublishedDate;
            if (detailedBookInfo.has("publishedDate")){

                bookPublishedDate = detailedBookInfo.getString("publishedDate");

            } else {
                bookPublishedDate = "No published date";
            }

            //Extract the value for the key called "previewLink"
            String bookPreviewLink = detailedBookInfo.getString("previewLink");

            //Extract the value for the key called "imageLinks"
            JSONObject bookLinks = detailedBookInfo.getJSONObject("imageLinks");
            String bookImageLink = bookLinks.getString("thumbnail");

            //Extract the value for the key called "description"
            String bookDescription = detailedBookInfo.getString("description");

            //Extract the value fir the key called "pageCount"
            int bookPages = detailedBookInfo.getInt("pageCount");

            // Create a new {@link Book} object with the params
            // from the JSON response.
            bookDetails = new Book(bookAuthor, bookTitle, bookRating, bookLink, bookPublisher,
                    bookPublishedDate, bookPreviewLink, bookImageLink, bookDescription, bookPages);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the Book JSON results", e);
        }
        return bookDetails;
    }
}
