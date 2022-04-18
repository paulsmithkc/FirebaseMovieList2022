package edu.ranken.prsmith.movielist2022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import edu.ranken.prsmith.movielist2022.ui.movie.MovieDetailsViewModel;
import edu.ranken.prsmith.movielist2022.ui.review.ReviewListAdapter;

public class MovieDetailsActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE_ID = "movieId";

    // views
    private FloatingActionButton composeReviewButton;
    private FloatingActionButton shareGameButton;
    private TextView movieErrorText;
    private TextView movieTitleText;
    private TextView movieDescriptionText;
    private ImageView movieBanner;
    private ImageView[] movieScreenshots;
    private TextView reviewErrorText;
    private TextView reviewCountText;
    private RecyclerView reviewRecylerView;

    // state
    private String movieId;
    private Movie movie;
    private MovieDetailsViewModel model;
    private Picasso picasso;
    private LinearLayoutManager reviewLayoutManager;
    private ReviewListAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_scroll);

        // find views
        composeReviewButton = findViewById(R.id.composeReviewButton);
        shareGameButton = findViewById(R.id.shareGameButton);
        movieErrorText = findViewById(R.id.movieErrorText);
        movieTitleText = findViewById(R.id.movieTitleText);
        movieDescriptionText = findViewById(R.id.movieDescriptionText);
        movieBanner = findViewById(R.id.movieBannerImage);
        movieScreenshots = new ImageView[] {
            findViewById(R.id.movieScreenshot1),
            findViewById(R.id.movieScreenshot2),
            findViewById(R.id.movieScreenshot3)
        };
        reviewErrorText = findViewById(R.id.movieReviewErrorText);
        reviewCountText = findViewById(R.id.movieReviewCountText);
        reviewRecylerView = findViewById(R.id.movieReviewList);

        // get picasso
        picasso = Picasso.get();

        // get model
        model = new ViewModelProvider(this).get(MovieDetailsViewModel.class);

        // setup recycler view and adapter
        reviewLayoutManager = new LinearLayoutManager(this);
        reviewAdapter = new ReviewListAdapter(this);
        reviewRecylerView.setLayoutManager(reviewLayoutManager);
        reviewRecylerView.setAdapter(reviewAdapter);

        // bind model
        model.getMovie().observe(this, (movie) -> {
            this.movie = movie;

            if (movie == null || movie.name == null) {
                movieTitleText.setText(R.string.nameMissing);
            } else {
                movieTitleText.setText(movie.name);
            }

            if (movie == null || movie.longDescription == null) {
                movieDescriptionText.setText(R.string.noDescription);
            } else {
                movieDescriptionText.setText(movie.longDescription);
            }

            if (movie == null || movie.bannerUrl == null) {
                movieBanner.setImageResource(R.drawable.placeholder_banner);
            } else {
                movieBanner.setImageResource(R.drawable.ic_downloading);
                picasso
                    .load(movie.bannerUrl)
                    .noPlaceholder()
                    //.placeholder(R.drawable.ic_downloading)
                    .error(R.drawable.ic_error)
                    .resizeDimen(R.dimen.movieBannerResizeWidth, R.dimen.movieBannerResizeHeight)
                    .centerInside()
                    .into(movieBanner);
            }

            if (movie == null || movie.screenshots == null) {
                for (int i = 0; i < movieScreenshots.length; ++i) {
                    movieScreenshots[i].setImageResource(R.drawable.placeholder_screenshot);
                }
            } else {
                for (int i = 0; i < movieScreenshots.length; ++i) {
                    ImageView imageView = movieScreenshots[i];
                    if (i >= movie.screenshots.size()) {
                        imageView.setImageResource(R.drawable.placeholder_screenshot);
                    } else {
                        imageView.setImageResource(R.drawable.ic_downloading);
                        picasso
                            .load(movie.screenshots.get(i))
                            .noPlaceholder()
                            //.placeholder(R.drawable.ic_downloading)
                            .error(R.drawable.ic_error)
                            .resizeDimen(R.dimen.movieScreenshotResizeWidth, R.dimen.movieScreenshotResizeHeight)
                            .centerInside()
                            .into(imageView);
                    }
                }
            }
        });
        model.getReviews().observe(this, (reviews) -> {
            reviewAdapter.setReviews(reviews);
            if (reviews == null) {
                reviewCountText.setVisibility(View.GONE);
                reviewCountText.setText("");
            } else {
                Resources res = getResources();

                int reviewCount = reviews.size();
                String reviewPlural = res.getQuantityString(R.plurals.review, reviewCount);
                reviewCountText.setVisibility(View.VISIBLE);
                reviewCountText.setText(getString(R.string.reviewCountText, reviewCount, reviewPlural));

//                // WARNING: code below is only for debugging
//                reviewRecylerView.post(() -> {
//                    int recyclerHeight = reviewRecylerView.getMeasuredHeight();
//                    int activityHeight = findViewById(android.R.id.content).getMeasuredHeight();
//                    reviewCountText.append("\nDEBUG height " + recyclerHeight + "px of " + activityHeight + "px");
//                    int firstVisiblePos = reviewLayoutManager.findFirstVisibleItemPosition();
//                    int lastVisiblePos = reviewLayoutManager.findLastVisibleItemPosition();
//                    reviewCountText.append("\nDEBUG visible " + firstVisiblePos + " to " + lastVisiblePos);
//                });
            }
        });
        model.getMovieError().observe(this, (messageId) -> {
            if (messageId != null) {
                movieErrorText.setText(messageId);
                movieErrorText.setVisibility(View.VISIBLE);
            } else {
                movieErrorText.setText(null);
                movieErrorText.setVisibility(View.GONE);
            }
        });
        model.getReviewsError().observe(this, (messageId) -> {
            if (messageId != null) {
                reviewErrorText.setText(messageId);
                reviewErrorText.setVisibility(View.VISIBLE);
            } else {
                reviewErrorText.setText(null);
                reviewErrorText.setVisibility(View.GONE);
            }
        });
        model.getSnackbarMessage().observe(this, (messageId) -> {
            if (messageId != null) {
                Snackbar.make(movieTitleText, messageId, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            } else {
                // no message to show
            }
        });

        // register listeners
        composeReviewButton.setOnClickListener((view) -> {
            // TODO: implement compose review activity
            Log.i(LOG_TAG, "Compose review clicked.");
            Snackbar.make(view, R.string.notImplemented, Snackbar.LENGTH_SHORT).show();
        });
        shareGameButton.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "Share game clicked.");

            if (movie == null) {
                Snackbar.make(view, R.string.errorMovieNotFound, Snackbar.LENGTH_SHORT).show();
            } else if (movie.name == null) {
                Snackbar.make(view, R.string.movieHasNoName, Snackbar.LENGTH_SHORT).show();
            } else {
                String movieName;
                if (movie.releaseYear == null) {
                    movieName = movie.name;
                } else {
                    movieName = movie.name + " (" + movie.releaseYear + ")";
                }

                String message =
                    getString(R.string.shareGameMessage) +
                    movieName +
                    "\nhttps://my-movie-list.com/movie/" + movie.id;

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                // sendIntent.putExtra(Intent.EXTRA_TITLE, movieName);
                // sendIntent.setData(Uri.parse(movie.image));
                sendIntent.setType("text/plain");

                startActivity(Intent.createChooser(sendIntent, getString(R.string.shareGame)));
            }
        });

        if (savedInstanceState == null) {
            // get intent
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
            Log.i(LOG_TAG, "movieId: " + movieId);
            movieId = savedInstanceState.getString("movieId");
            movie = model.getMovie().getValue();
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