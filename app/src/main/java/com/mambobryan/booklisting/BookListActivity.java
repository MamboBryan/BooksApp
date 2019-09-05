package com.mambobryan.booklisting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {

    /**
     * URL for the popular Book data
     */
    public static String GOOGLE_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";
    private ListView mResultsList;
    private BookAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        //Get the searched book string
        Intent homeIntent = getIntent();
        String query = homeIntent.getStringExtra("query");

        //Add the string to the last part of the string
        GOOGLE_REQUEST_URL = GOOGLE_REQUEST_URL + query;

        mResultsList = findViewById(R.id.results_listView);
        mAdapter = new BookAdapter(this,
                android.R.layout.simple_list_item_1, new ArrayList<Book>());


        mResultsList.setAdapter(mAdapter);

        BookListAsyncTask task = new BookListAsyncTask();
        task.execute(GOOGLE_REQUEST_URL);

        mResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book bookItem = (Book) adapterView.getItemAtPosition(i);
                String bookItemQuery = bookItem.getLink();

                Intent listIntent =
                        new Intent(BookListActivity.this, BookDetailsActivity.class);
                listIntent.putExtra("query", bookItemQuery);
                startActivity(listIntent);
            }
        });

    }

    public class BookListAsyncTask extends AsyncTask<String, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Book> result = QueriesUtil.fetchBookData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Book> data) {
            // Clear the adapter of previous earthquake data
            mAdapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
}
