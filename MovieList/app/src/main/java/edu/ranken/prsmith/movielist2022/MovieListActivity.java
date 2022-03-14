package edu.ranken.prsmith.movielist2022;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import edu.ranken.prsmith.movielist2022.data.Genre;
import edu.ranken.prsmith.movielist2022.data.MovieList;
import edu.ranken.prsmith.movielist2022.ui.MovieListAdapter;
import edu.ranken.prsmith.movielist2022.ui.MovieListViewModel;
import edu.ranken.prsmith.movielist2022.ui.SpinnerOption;

public class MovieListActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_movie_list);

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
            new SpinnerOption<>(getString(R.string.allMovies), MovieList.ALL_MOVIES),
            new SpinnerOption<>(getString(R.string.myVotes), MovieList.MY_VOTES),
            new SpinnerOption<>(getString(R.string.myUpvotes), MovieList.MY_UPVOTES),
            new SpinnerOption<>(getString(R.string.myDownvotes), MovieList.MY_DOWNVOTES)
        };
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listOptions);
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
                int selectedPosition = 0;
                String selectedId = model.getFilterGenreId();

                ArrayList<SpinnerOption<String>> genreNames = new ArrayList<>(genres.size());
                genreNames.add(new SpinnerOption<>(getString(R.string.allGenres), null));

                for (int i = 0; i < genres.size(); ++i) {
                    Genre genre = genres.get(i);
                    if (genre.id != null && genre.name != null) {
                        genreNames.add(new SpinnerOption<>(genre.name, genre.id));
                        if (Objects.equals(genre.id, selectedId)) {
                            selectedPosition = genreNames.size() - 1;
                        }
                    }
                }

                genresAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genreNames);
                genreSpinner.setAdapter(genresAdapter);
                genreSpinner.setSelection(selectedPosition, false);
            }
        });
        model.getErrorMessage().observe(this, (errorMessage) -> {
            // FIXME: hide error message when it is null/empty
            errorText.setVisibility(errorMessage != null ? View.VISIBLE : View.GONE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as back navigation
            onBackPressed();
            return true;
        } else if (itemId == R.id.actionSignOut) {
            onSignOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.i(LOG_TAG, "Back Pressed");
    }

    public void onSignOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener((result) -> {
                Log.i(LOG_TAG, "Signed out.");
                finish();
            });
    }
}