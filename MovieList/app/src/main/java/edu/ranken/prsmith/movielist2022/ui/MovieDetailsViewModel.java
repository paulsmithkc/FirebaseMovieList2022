package edu.ranken.prsmith.movielist2022.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

import edu.ranken.prsmith.movielist2022.data.Movie;
import edu.ranken.prsmith.movielist2022.data.Review;

public class MovieDetailsViewModel extends ViewModel {
    private static final String LOG_TAG = MovieDetailsViewModel.class.getSimpleName();

    // firebase
    private final FirebaseFirestore db;
    private ListenerRegistration movieRegistration;
    private ListenerRegistration reviewRegistration;
    private String movieId;
    private String username = "prsmith";  // FIXME: implement a login screen, and use the logged in user's id

    // live data
    private final MutableLiveData<String> snackbarMessage;
    private final MutableLiveData<Movie> movie;
    private final MutableLiveData<String> movieError;
    private final MutableLiveData<List<Review>> reviews;
    private final MutableLiveData<String> reviewError;

    public MovieDetailsViewModel() {
        db = FirebaseFirestore.getInstance();

        // live data
        snackbarMessage = new MutableLiveData<>(null);
        movie = new MutableLiveData<>(null);
        movieError = new MutableLiveData<>(null);
        reviews = new MutableLiveData<>(null);
        reviewError = new MutableLiveData<>(null);
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
    public LiveData<String> getSnackbarMessage() { return snackbarMessage; }
    public LiveData<Movie> getMovie() { return movie; }
    public LiveData<String> getMovieError() { return movieError; }
    public LiveData<List<Review>> getReviews() { return reviews; }
    public LiveData<String> getReviewError() { return reviewError; }

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
            this.movieError.postValue("No movie selected.");
            this.snackbarMessage.postValue("No movie selected.");
        } else {
            movieRegistration =
                db.collection("movies")
                    .document(movieId)
                    .addSnapshotListener((document, error) -> {
                        boolean isMovieLoaded = movie.getValue() != null;

                        if (error != null) {
                            Log.e(LOG_TAG, "Error getting movie.", error);
                            this.movieError.postValue("Error getting movie.");
                            this.snackbarMessage.postValue("Error getting movie.");
                        } else if (document != null && document.exists()) {
                            Movie newMovie = document.toObject(Movie.class);
                            this.movie.postValue(newMovie);
                            this.movieError.postValue(null);
                            // don't show this message the first time
                            // so that it doesn't cover the FAB
                            if (isMovieLoaded) {
                                this.snackbarMessage.postValue("Movie updated.");
                            }
                        } else {
                            this.movie.postValue(null);
                            this.movieError.postValue("Movie does not exist.");
                            this.snackbarMessage.postValue("Movie does not exist.");
                        }
                    });

            reviewRegistration =
                db.collection("reviews")
                    .whereEqualTo("movieId", movieId)
                    .orderBy("publishedOn", Query.Direction.DESCENDING)
                    .addSnapshotListener((querySnapshot, error) -> {
                        boolean areReviewsLoaded = reviews.getValue() != null;

                        if (error != null) {
                            Log.e(LOG_TAG, "Error getting movie.", error);
                            this.reviewError.postValue("Error getting reviews.");
                            this.snackbarMessage.postValue("Error getting reviews.");
                        } else if (querySnapshot != null) {
                            List<Review> newReviews = querySnapshot.toObjects(Review.class);
                            this.reviews.postValue(newReviews);
                            this.reviewError.postValue(null);
                            // don't show this message the first time
                            // so that it doesn't cover the FAB
                            if (areReviewsLoaded) {
                                this.snackbarMessage.postValue("Reviews updated.");
                            }
                        }
                    });
        }
    }
}
