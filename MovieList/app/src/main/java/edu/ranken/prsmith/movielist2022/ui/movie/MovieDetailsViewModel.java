package edu.ranken.prsmith.movielist2022.ui.movie;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ranken.prsmith.movielist2022.R;
import edu.ranken.prsmith.movielist2022.data.Movie;
import edu.ranken.prsmith.movielist2022.data.Review;

public class MovieDetailsViewModel extends ViewModel {

    // constants
    private static final String LOG_TAG = MovieDetailsViewModel.class.getSimpleName();

    // firebase
    private final FirebaseFirestore db;
    private ListenerRegistration movieRegistration;
    private ListenerRegistration reviewRegistration;

    // data
    private String movieId;

    // live data
    private final MutableLiveData<Integer> snackbarMessage;
    private final MutableLiveData<Movie> movie;
    private final MutableLiveData<Integer> movieError;
    private final MutableLiveData<List<Review>> reviews;
    private final MutableLiveData<Integer> reviewsError;

    public MovieDetailsViewModel() {
        db = FirebaseFirestore.getInstance();

        // live data
        snackbarMessage = new MutableLiveData<>(null);
        movie = new MutableLiveData<>(null);
        movieError = new MutableLiveData<>(null);
        reviews = new MutableLiveData<>(null);
        reviewsError = new MutableLiveData<>(null);
    }

    @Override
    protected void onCleared() {
        if (movieRegistration != null) {
            movieRegistration.remove();
        }
        if (reviewRegistration != null) {
            reviewRegistration.remove();
        }
        super.onCleared();
    }

    public String getMovieId() { return movieId; }
    public LiveData<Integer> getSnackbarMessage() { return snackbarMessage; }
    public LiveData<Movie> getMovie() { return movie; }
    public LiveData<Integer> getMovieError() { return movieError; }
    public LiveData<List<Review>> getReviews() { return reviews; }
    public LiveData<Integer> getReviewsError() { return reviewsError; }

    // remove snackbar message
    public void clearSnackbar() { snackbarMessage.postValue(null); }

    // fetch a particular movie from the database
    public void fetchMovie(String movieId) {
        this.movieId = movieId;

        if (movieRegistration != null) {
            movieRegistration.remove();
        }
        if (reviewRegistration != null) {
            reviewRegistration.remove();
        }

        if (movieId == null) {
            this.movie.postValue(null);
            this.movieError.postValue(R.string.errorNoMovieSelected);
            this.snackbarMessage.postValue(R.string.errorNoMovieSelected);
        } else {
            movieRegistration =
                db.collection("movies")
                    .document(movieId)
                    .addSnapshotListener((document, error) -> {
                        boolean isMovieLoaded = movie.getValue() != null;

                        if (error != null) {
                            Log.e(LOG_TAG, "Error getting movie.", error);
                            this.movieError.postValue(R.string.errorFetchingMovie);
                            this.snackbarMessage.postValue(R.string.errorFetchingMovie);
                        } else if (document != null && document.exists()) {
                            Movie newMovie = document.toObject(Movie.class);
                            this.movie.postValue(newMovie);
                            this.movieError.postValue(null);

                            // don't show this message the first time
                            // so that it doesn't cover the FAB
                            if (isMovieLoaded) {
                                this.snackbarMessage.postValue(R.string.movieUpdated);
                            }
                        } else {
                            this.movie.postValue(null);
                            this.movieError.postValue(R.string.errorMovieDoesNotExist);
                            this.snackbarMessage.postValue(R.string.errorMovieDoesNotExist);
                        }
                    });

            reviewRegistration =
                db.collection("reviews")
                    .whereEqualTo("movieId", movieId)
                    .orderBy("publishedOn", Query.Direction.DESCENDING)
                    .addSnapshotListener((querySnapshot, error) -> {
                        boolean areReviewsLoaded = reviews.getValue() != null;

                        if (error != null) {
                            Log.e(LOG_TAG, "Error getting reviews.", error);
                            this.reviewsError.postValue(R.string.errorFetchingReviews);
                            this.snackbarMessage.postValue(R.string.errorFetchingReviews);
                        } else if (querySnapshot != null) {
                            //List<Review> newReviews = querySnapshot.toObjects(Review.class);

                            // populate list with 100 fake reviews
                            List<Review> newReviews = new ArrayList<>();
                            for (int i = 0; i < 100; ++i) {
                                Review review = new Review();
                                review.movieId = movieId;
                                review.username = "reviewer" + (i+1);
                                review.id = review.username + ";" + review.movieId;
                                review.reviewText = "lorem ipsum";
                                review.publishedOn = new Date();
                                review.publishedOn.setDate(review.publishedOn.getDate() - i);
                                newReviews.add(review);
                            }

                            this.reviews.postValue(newReviews);
                            this.reviewsError.postValue(null);

                            // don't show this message the first time
                            // so that it doesn't cover the FAB
                            if (areReviewsLoaded) {
                                this.snackbarMessage.postValue(R.string.reviewsUpdated);
                            }
                        }
                    });
        }
    }
}
