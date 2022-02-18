package edu.ranken.prsmith.movielist2022;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import edu.ranken.prsmith.movielist2022.ui.MovieListAdapter;
import edu.ranken.prsmith.movielist2022.ui.MovieListViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    // views
    private TextView errorText;
    private RecyclerView recyclerView;

    // state
    private MovieListViewModel model;
    private MovieListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        errorText = findViewById(R.id.errorText);
        recyclerView = findViewById(R.id.movieList);

        // setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // setup view model and adapter
        model = new ViewModelProvider(this).get(MovieListViewModel.class);
        adapter = new MovieListAdapter(this, model);
        recyclerView.setAdapter(adapter);

        // observe model
        model.getMovies().observe(this, (movies) -> {
            adapter.setItems(movies);
        });
        model.getVotes().observe(this, (votes) -> {
            adapter.setVotes(votes);
        });
        model.getErrorMessage().observe(this, (errorMessage) -> {
            errorText.setText(errorMessage);
        });
        model.getSnackbarMessage().observe(this, (snackbarMessage) -> {
            if (snackbarMessage != null) {
                Snackbar.make(recyclerView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            }
        });
    }
}