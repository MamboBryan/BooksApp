package com.mambobryan.booklisting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity{

    public static final String LOG_TAG = HomeActivity.class.getName();

    /**
     * URL for the popular Book data
     */
    public static final String GOOGLE_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=money";

    private BookAdapter mAdapter;
    private ListView mBooksListView;
    private String mSearchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mBooksListView = findViewById(R.id.home_list);

        mAdapter = new
                BookAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Book>());

        mBooksListView.setAdapter(mAdapter);

        BookAsyncTask task = new BookAsyncTask();
        task.execute(GOOGLE_REQUEST_URL);

        //Find the search editText
        final EditText searchEditText = findViewById(R.id.search_editText);


        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                String bookToSearch = searchEditText.getText().toString();

                if (TextUtils.isEmpty(bookToSearch)) {
                    Toast.makeText(HomeActivity.this,
                            "Enter book to search", Toast.LENGTH_SHORT).show();
                } else {
                    mSearchQuery = searchEditText.getText().toString();
                    Intent listIntent =
                            new Intent(HomeActivity.this, BookListActivity.class);
                    listIntent.putExtra("query", mSearchQuery);
                    startActivity(listIntent);
                }
                return true;
            }
        });

        mBooksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book bookItem = (Book) adapterView.getItemAtPosition(i);
                mSearchQuery = bookItem.getLink();

                Intent listIntent =
                        new Intent(HomeActivity.this, BookDetailsActivity.class);
                listIntent.putExtra("query", mSearchQuery);
                startActivity(listIntent);
            }
        });

    }

    public class BookAsyncTask extends AsyncTask<String, Void, List<Book>>{

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
