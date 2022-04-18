package edu.ranken.prsmith.movielist2022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import edu.ranken.prsmith.movielist2022.data.Movie;
import edu.ranken.prsmith.movielist2022.ui.movie.MovieDetailsFragment;
import edu.ranken.prsmith.movielist2022.ui.movie.MovieDetailsViewModel;
import edu.ranken.prsmith.movielist2022.ui.review.ReviewListAdapter;

public class MovieDetailsActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE_ID = "movieId";

    // views
    private FragmentContainerView fragmentContainer;
    private MovieDetailsFragment fragment;
    private MovieDetailsViewModel model;

    // state
    private String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        fragmentContainer = findViewById(R.id.fragmentContainer);
        fragment = new MovieDetailsFragment();
        model = new ViewModelProvider(this).get(MovieDetailsViewModel.class);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String intentAction = intent.getAction();
            Uri intentData = intent.getData();

            if (intentAction == null) {
                movieId = intent.getStringExtra(EXTRA_MOVIE_ID);
                model.fetchMovie(movieId);
            } else if (Objects.equals(intentAction, Intent.ACTION_VIEW) && intentData != null) {
                handleWebLink(intent);
            }
        } else {
            movieId = savedInstanceState.getString("movieId");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        outState.putString("movieId", movieId);
        // outState.putParcelable("movie", movie);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as back navigation
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getAction() != null) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(new Intent(this, LoginActivity.class));
            stackBuilder.addNextIntent(new Intent(this, HomeActivity.class));
            stackBuilder.startActivities();
        } else {
            super.onBackPressed();
        }
    }

    private void handleWebLink(Intent intent) {
        Uri uri = intent.getData();
        String path = uri.getPath();
        String prefix = "/movie/";

        // parse uri path
        if (path.startsWith(prefix)) {
            int movieIdEnd = path.indexOf("/", prefix.length());
            if (movieIdEnd < 0) {
                movieId = path.substring(prefix.length());
            } else {
                movieId = path.substring(prefix.length(), movieIdEnd);
            }
        } else {
            movieId = null;
        }

//        // create synthetic back stack
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addNextIntent(new Intent(this, LoginActivity.class));
//        stackBuilder.addNextIntent(new Intent(this, HomeActivity.class));
//        stackBuilder.addNextIntent(new Intent(this, MovieDetailsActivity.class).putExtra(EXTRA_MOVIE_ID, movieId));
//        stackBuilder.startActivities();

        // load movie data
        model.fetchMovie(movieId);
    }
}