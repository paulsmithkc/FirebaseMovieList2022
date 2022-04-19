package edu.ranken.prsmith.movielist2022.ui.movie;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import edu.ranken.prsmith.movielist2022.ComposeReviewActivity;
import edu.ranken.prsmith.movielist2022.R;
import edu.ranken.prsmith.movielist2022.data.Movie;
import edu.ranken.prsmith.movielist2022.ui.review.ReviewListAdapter;

public class MovieDetailsFragment extends Fragment {

    // constants
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

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

    public MovieDetailsFragment() {
        super(R.layout.movie_details_scroll);
    }

    @Override
    public void onViewCreated(@NonNull View contentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(contentView, savedInstanceState);

        // find views
        composeReviewButton = contentView.findViewById(R.id.composeReviewButton);
        shareGameButton = contentView.findViewById(R.id.shareGameButton);
        movieErrorText = contentView.findViewById(R.id.movieErrorText);
        movieTitleText = contentView.findViewById(R.id.movieTitleText);
        movieDescriptionText = contentView.findViewById(R.id.movieDescriptionText);
        movieBanner = contentView.findViewById(R.id.movieBannerImage);
        movieScreenshots = new ImageView[] {
            contentView.findViewById(R.id.movieScreenshot1),
            contentView.findViewById(R.id.movieScreenshot2),
            contentView.findViewById(R.id.movieScreenshot3)
        };
        reviewErrorText = contentView.findViewById(R.id.movieReviewErrorText);
        reviewCountText = contentView.findViewById(R.id.movieReviewCountText);
        reviewRecylerView = contentView.findViewById(R.id.movieReviewList);

        // get picasso
        picasso = Picasso.get();

        // setup recycler view and adapter
        FragmentActivity activity = getActivity();
        reviewLayoutManager = new LinearLayoutManager(activity);
        reviewAdapter = new ReviewListAdapter(activity);
        reviewRecylerView.setLayoutManager(reviewLayoutManager);
        reviewRecylerView.setAdapter(reviewAdapter);

        // bind model
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        model = new ViewModelProvider(activity).get(MovieDetailsViewModel.class);
        model.getMovie().observe(lifecycleOwner, (movie) -> {
            this.movieId = model.getMovieId();
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
        model.getReviews().observe(lifecycleOwner, (reviews) -> {
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
        model.getMovieError().observe(lifecycleOwner, (messageId) -> {
            if (messageId != null) {
                movieErrorText.setText(messageId);
                movieErrorText.setVisibility(View.VISIBLE);
            } else {
                movieErrorText.setText(null);
                movieErrorText.setVisibility(View.GONE);
            }
        });
        model.getReviewsError().observe(lifecycleOwner, (messageId) -> {
            if (messageId != null) {
                reviewErrorText.setText(messageId);
                reviewErrorText.setVisibility(View.VISIBLE);
            } else {
                reviewErrorText.setText(null);
                reviewErrorText.setVisibility(View.GONE);
            }
        });
        model.getSnackbarMessage().observe(lifecycleOwner, (messageId) -> {
            if (messageId != null) {
                Snackbar.make(movieTitleText, messageId, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            } else {
                // no message to show
            }
        });

        // register listeners
        composeReviewButton.setOnClickListener((v) -> {
            Log.i(LOG_TAG, "Compose review clicked.");

            Intent intent = new Intent(activity, ComposeReviewActivity.class);
            intent.putExtra(ComposeReviewActivity.EXTRA_MOVIE_ID, movieId);
            startActivity(intent);
        });
        shareGameButton.setOnClickListener((v) -> {
            Log.i(LOG_TAG, "Share game clicked.");

            if (movie == null) {
                Snackbar.make(v, R.string.errorMovieNotFound, Snackbar.LENGTH_SHORT).show();
            } else if (movie.name == null) {
                Snackbar.make(v, R.string.movieHasNoName, Snackbar.LENGTH_SHORT).show();
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
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        outState.putString("movieId", movieId);
        // outState.putParcelable("movie", movie);
    }
}
