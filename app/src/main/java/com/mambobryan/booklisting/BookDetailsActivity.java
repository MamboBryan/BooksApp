package com.mambobryan.booklisting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BookDetailsActivity extends AppCompatActivity {

    private static String GOOGLE_API_QUERY;
    private TextView mBookAuthor;
    private TextView mBookTitle;
    private TextView mBookRating;
    private TextView mBookPublisher;
    private TextView mBookPublishedDate;
    private Button mBookPreview;
    private TextView mBookDescription;
    private TextView mBookPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        mBookAuthor = findViewById(R.id.bookAuthor);
        mBookTitle = findViewById(R.id.bookTitle);
        mBookRating = findViewById(R.id.bookRating);
        mBookPublisher = findViewById(R.id.bookPublisher);
        mBookPublishedDate = findViewById(R.id.bookPublishedDate);
        mBookDescription = findViewById(R.id.bookDescription);
        mBookPages = findViewById(R.id.bookPagesCount);

        mBookPreview = findViewById(R.id.readmoreButton);

        Intent myIntent = getIntent();
        GOOGLE_API_QUERY = myIntent.getStringExtra("query");

        BookDetailsAsyncTask task = new BookDetailsAsyncTask();
        task.execute(GOOGLE_API_QUERY);


    }

    public class BookDetailsAsyncTask extends AsyncTask<String, Void, Book> {

        @Override
        protected Book doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            return DetailQueryUtil.fetchBookData(urls[0]);
        }

        @Override
        protected void onPostExecute(final Book book) {

            mBookAuthor.setText(book.getAuthor());
            mBookTitle.setText(book.getTitle());
            int rating = book.getRating().intValue();
            mBookRating.setText(String.valueOf(rating));
            mBookPublisher.setText(book.getPublisher());
            mBookPublishedDate.setText(book.getPublishedDate());
            mBookDescription.setText(book.getDescription());
            mBookPages.setText(String.valueOf(book.getPageCount()));

            mBookPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String bookURL = book.getPreviewLink();

                    Uri webpage = Uri.parse(bookURL);
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(websiteIntent);
                    }

                }
            });

        }
    }
}
