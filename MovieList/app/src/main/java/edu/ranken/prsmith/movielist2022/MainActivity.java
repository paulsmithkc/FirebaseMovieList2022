package edu.ranken.prsmith.movielist2022;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import edu.ranken.prsmith.movielist2022.data.Genre;
import edu.ranken.prsmith.movielist2022.data.GenreFilter;
import edu.ranken.prsmith.movielist2022.ui.MovieListAdapter;
import edu.ranken.prsmith.movielist2022.ui.MovieListViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    // views
    private Spinner genreSpinner;
    private Spinner listSpinner;
    private TextView errorText;
    private RecyclerView recyclerView;

    // state
    private MovieListViewModel model;
    private MovieListAdapter moviesAdapter;
    private ArrayAdapter<GenreFilter> genresAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        genreSpinner = findViewById(R.id.genreSpinner);
        listSpinner = findViewById(R.id.listSpinner);
        errorText = findViewById(R.id.errorText);
        recyclerView = findViewById(R.id.movieList);

        // setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // setup view model and adapter
        model = new ViewModelProvider(this).get(MovieListViewModel.class);
        moviesAdapter = new MovieListAdapter(this, model);
        recyclerView.setAdapter(moviesAdapter);

        // observe model
        model.getMovies().observe(this, (movies) -> {
            moviesAdapter.setItems(movies);
        });
        model.getVotes().observe(this, (votes) -> {
            moviesAdapter.setVotes(votes);
        });
        model.getGenres().observe(this, (genres) -> {
            if (genres != null) {
                ArrayList<GenreFilter> genreNames = new ArrayList<>(genres.size());
                genreNames.add(new GenreFilter(
                    null,
                    getString(R.string.allGenres)
                ));

                for (Genre genre : genres) {
                    genreNames.add(new GenreFilter(genre.id, genre.name));
                }

                genresAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genreNames);
                genreSpinner.setAdapter(genresAdapter);
            }
        });
        model.getErrorMessage().observe(this, (errorMessage) -> {
            errorText.setVisibility(errorMessage != null ? View.VISIBLE : View.GONE);
            errorText.setText(errorMessage);
        });
        model.getSnackbarMessage().observe(this, (snackbarMessage) -> {
            if (snackbarMessage != null) {
                Snackbar.make(recyclerView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            }
        });

        // register listeners
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GenreFilter genre = (GenreFilter) parent.getItemAtPosition(position);
                model.filterMoviesByGenre(genre.genreId);
                Log.i(LOG_TAG, "Filter by genre: " + genre.genreId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
    }
}