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
import edu.ranken.prsmith.movielist2022.data.MovieList;
import edu.ranken.prsmith.movielist2022.ui.MovieListAdapter;
import edu.ranken.prsmith.movielist2022.ui.MovieListViewModel;
import edu.ranken.prsmith.movielist2022.ui.SpinnerOption;

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
    private ArrayAdapter<SpinnerOption<String>> genresAdapter;
    private ArrayAdapter<SpinnerOption<MovieList>> listAdapter;

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

        // populate list spinner
        SpinnerOption<MovieList>[] listOptions = new SpinnerOption[] {
            new SpinnerOption<>("All Movies", MovieList.ALL_MOVIES),
            new SpinnerOption<>("My Votes", MovieList.MY_VOTES),
            new SpinnerOption<>("My Upvotes", MovieList.MY_UPVOTES),
            new SpinnerOption<>("My Downvotes", MovieList.MY_DOWNVOTES)
        };
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listOptions);
        listSpinner.setAdapter(listAdapter);

        // observe model
        model.getMovies().observe(this, (movies) -> {
            moviesAdapter.setMovies(movies);
        });
        model.getVotes().observe(this, (votes) -> {
            moviesAdapter.setVotes(votes);
        });
        model.getGenres().observe(this, (genres) -> {
            if (genres != null) {
                // FIXME: preserve selected item

                ArrayList<SpinnerOption<String>> genreNames = new ArrayList<>(genres.size());
                genreNames.add(new SpinnerOption<>(getString(R.string.allGenres), null));

                for (Genre genre : genres) {
                    // FIXME: filter out genres that do not have a name
                    genreNames.add(new SpinnerOption<>(genre.name, genre.id));
                }

                genresAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genreNames);
                genreSpinner.setAdapter(genresAdapter);
            }
        });
        model.getErrorMessage().observe(this, (errorMessage) -> {
            // FIXME: hide error message when it is null/empty
            errorText.setText(errorMessage);
        });
        model.getSnackbarMessage().observe(this, (snackbarMessage) -> {
            // Only show a snackbar, when there is a message to be shown
            if (snackbarMessage != null) {
                Snackbar.make(recyclerView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            }
        });

        // register listeners
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption<String> option = (SpinnerOption<String>) parent.getItemAtPosition(position);
                model.filterMoviesByGenre(option.getValue());
                Log.i(LOG_TAG, "Filter by genre: " + option.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption<MovieList> option = (SpinnerOption<MovieList>) parent.getItemAtPosition(position);
                model.filterMoviesByList(option.getValue());
                Log.i(LOG_TAG, "Filter by list: " + option.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
    }
}