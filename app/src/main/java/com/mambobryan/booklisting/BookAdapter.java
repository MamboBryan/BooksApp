package com.mambobryan.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, int resource, List<Book> books) {
        super(context, resource, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,
                    parent, false);
        }

        //Get the Book object at the position in the list
        Book currentBook = getItem(position);

        TextView titleText = listItemView.findViewById(R.id.title_TextView);
        titleText.setText(currentBook.getTitle());

        TextView authorText = listItemView.findViewById(R.id.author_TextView);
        authorText.setText(currentBook.getAuthor());

        TextView ratingText = listItemView.findViewById(R.id.rating_TextView);
        CardView ratingCardView = listItemView.findViewById(R.id.rating_cardView);
        int rating = currentBook.getRating().intValue();

        ratingCardView.setCardBackgroundColor(getBackgroundColor(rating));
        ratingText.setText(String.valueOf(rating));


        return listItemView;
    }

    private int getBackgroundColor(int rating) {
        int backgroundColor;
        switch (rating) {
            case 0:
                backgroundColor = ContextCompat.getColor(getContext(), R.color.orange);
                break;
            case 1:
                backgroundColor = ContextCompat.getColor(getContext(), R.color.red);
                break;
            case 2:
                backgroundColor = ContextCompat.getColor(getContext(), R.color.purple);
                break;
            case 3:
                backgroundColor = ContextCompat.getColor(getContext(), R.color.yellow);
                break;
            case 4:
                backgroundColor = ContextCompat.getColor(getContext(), R.color.pink);
                break;
            case 5:
                backgroundColor = ContextCompat.getColor(getContext(), R.color.green);
                break;
            default:
                backgroundColor = ContextCompat.getColor(getContext(), R.color.primaryTextColor);
                break;
        }
        return backgroundColor;
    }
}
