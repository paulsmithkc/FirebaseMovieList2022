package edu.ranken.prsmith.movielist2022.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import edu.ranken.prsmith.movielist2022.data.Movie;

public class MovieDetailsViewModel extends ViewModel {
    private static final String LOG_TAG = MovieDetailsViewModel.class.getSimpleName();

    // firebase
    private final FirebaseFirestore db;
    private ListenerRegistration movieRegistration;
    private String movieId;

    // live data
    private final MutableLiveData<Movie> movie;
    private final MutableLiveData<String> movieError;
    private final MutableLiveData<String> snackbarMessage;

    public MovieDetailsViewModel() {
        db = FirebaseFirestore.getInstance();

        // live data
        movie = new MutableLiveData<>(null);
        movieError = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);
    }

    @Override
    protected void onCleared() {
        if (movieRegistration != null) {
            movieRegistration.remove();
        }
        super.onCleared();
    }

    public String getMovieId() { return movieId; }
    public LiveData<Movie> getMovie() { return movie; }
    public LiveData<String> getMovieError() { return movieError; }
    public LiveData<String> getSnackbarMessage() { return snackbarMessage; }

    // remove snackbar message
    public void clearSnackbar() { snackbarMessage.postValue(null); }

    // fetch a particular movie from the database
    public void fetchMovie(String movieId) {
        this.movieId = movieId;

        if (movieRegistration != null) {
            movieRegistration.remove();
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
                            Movie movie = document.toObject(Movie.class);
                            this.movie.postValue(movie);
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
        }
    }
}
