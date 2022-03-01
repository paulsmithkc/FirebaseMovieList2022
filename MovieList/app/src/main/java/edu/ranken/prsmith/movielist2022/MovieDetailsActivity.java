package edu.ranken.prsmith.movielist2022;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import edu.ranken.prsmith.movielist2022.ui.MovieDetailsViewModel;

public class MovieDetailsActivity extends BaseActivity {

    // constants
    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE_ID = "movieId";

    // state
    private String movieId;
    private MovieDetailsViewModel model;

    // views
    private TextView movieErrorText;
    private TextView movieTitleText;
    private TextView movieDescriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // find views
        movieErrorText = findViewById(R.id.movieErrorText);
        movieTitleText = findViewById(R.id.movieTitleText);
        movieDescriptionText = findViewById(R.id.movieDescriptionText);

        // get intent
        Intent intent = getIntent();
        movieId = intent.getStringExtra(EXTRA_MOVIE_ID);

        // bind model
        model = new ViewModelProvider(this).get(MovieDetailsViewModel.class);
        model.fetchMovie(movieId);
        model.getMovie().observe(this, (movie) -> {
            if (movie == null) {
                movieTitleText.setText(null);
                movieDescriptionText.setText(null);
            } else {
                movieTitleText.setText(movie.name);
                if (movie.description == null) {
                    movieDescriptionText.setText(R.string.noDescription);
                } else {
                    movieDescriptionText.setText(movie.description);
                }
            }
        });
        model.getMovieError().observe(this, (movieError) -> {
            movieErrorText.setVisibility(movieError != null ? View.VISIBLE : View.GONE);
            movieErrorText.setText(movieError);
        });
        model.getSnackbarMessage().observe(this, (message) -> {
            if (message != null) {
                Snackbar.make(movieTitleText, message, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            }
        });
    }
}