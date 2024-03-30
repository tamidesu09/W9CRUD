package com.etcetera.quotology;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class QuoteAdapter extends ArrayAdapter<Quote> {

    public QuoteAdapter(Context context, List<Quote> quotes) {
        super(context, 0, quotes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.quote_item, parent, false);
        }

        Quote quote = getItem(position);

        TextView quoteTextView = convertView.findViewById(R.id.quoteTextView);
        TextView authorTextView = convertView.findViewById(R.id.authorTextView);

        if (quote != null) {
            quoteTextView.setText(quote.getQuote());
            authorTextView.setText(quote.getAuthor());
        }

        return convertView;
    }
}
