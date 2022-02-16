package edu.ranken.prsmith.movielist2022.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import edu.ranken.prsmith.movielist2022.data.Movie;

public class MovieListViewModel extends ViewModel {
    private static final String LOG_TAG = "MovieListViewModel";

    private FirebaseFirestore db;
    private ListenerRegistration moviesRegistration;

    // live data
    private MutableLiveData<List<Movie>> movies;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<String> snackbarMessage;

    public MovieListViewModel() {
        db = FirebaseFirestore.getInstance();

        // live data
        movies = new MutableLiveData<>(null);
        errorMessage = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);

        // observe movies collection
        moviesRegistration =
            db.collection("movies")
                .orderBy("releaseYear")
                .orderBy("name")
                .limit(100)
                .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e(LOG_TAG, "Error getting movies.", error);
                        errorMessage.postValue(error.getMessage());
                        snackbarMessage.postValue(error.getMessage());
                    } else {
                        List<Movie> newMovies =
                            querySnapshot != null ? querySnapshot.toObjects(Movie.class) : null;

                        movies.postValue(newMovies);
                        errorMessage.postValue(null);
                        snackbarMessage.postValue("Movies Updated.");
                    }
                });
    }

    @Override
    protected void onCleared() {
        if (moviesRegistration != null) {
            moviesRegistration.remove();
        }
        super.onCleared();
    }

    public LiveData<List<Movie>> getMovies() { return movies; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getSnackbarMessage() { return snackbarMessage; }

    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

}
