package com.etcetera.quotology;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView quotesListView;
    private List<Quote> quotesList;
    private QuoteAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quotesListView = findViewById(R.id.quotes_lv);
        quotesList = new ArrayList<>();
        adapter = new QuoteAdapter(this, quotesList);
        quotesListView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("quotes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quotesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Quote quote = dataSnapshot.getValue(Quote.class);
                    quotesList.add(quote);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("FirebaseError", "Failed to read value.", error.toException());
            }
        });

        // Add long click listener for delete
        quotesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to delete this quote?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Quote quote = quotesList.get(position);
                                databaseReference.child(quote.getKey()).removeValue();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

        // Add click listener for update
        quotesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_update_quote, null);
                builder.setView(dialogView);
                final EditText quoteEditText = dialogView.findViewById(R.id.editTextQuote);
                final EditText authorEditText = dialogView.findViewById(R.id.editTextAuthor);
                Quote quote = quotesList.get(position);
                quoteEditText.setText(quote.getQuote());
                authorEditText.setText(quote.getAuthor());

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newQuote = quoteEditText.getText().toString();
                        String newAuthor = authorEditText.getText().toString();
                        Quote updatedQuote = quotesList.get(position);
                        updatedQuote.setQuote(newQuote);
                        updatedQuote.setAuthor(newAuthor);
                        databaseReference.child(updatedQuote.getKey()).setValue(updatedQuote);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        // Add click listener for FAB
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the AddQuoteActivity
                Intent intent = new Intent(MainActivity.this, AddQuoteActivity.class);
                startActivity(intent);
            }
        });
    }
}
