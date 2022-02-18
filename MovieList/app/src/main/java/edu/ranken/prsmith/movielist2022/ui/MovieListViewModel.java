package edu.ranken.prsmith.movielist2022.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ranken.prsmith.movielist2022.data.Movie;
import edu.ranken.prsmith.movielist2022.data.MovieVoteValue;

public class MovieListViewModel extends ViewModel {
    private static final String LOG_TAG = "MovieListViewModel";

    private final FirebaseFirestore db;
    private final ListenerRegistration moviesRegistration;
    private final ListenerRegistration votesRegistration;
    private final String username = "prsmith";

    // live data
    private final MutableLiveData<List<Movie>> movies;
    private final MutableLiveData<List<MovieVoteValue>> votes;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<String> snackbarMessage;

    public MovieListViewModel() {
        db = FirebaseFirestore.getInstance();

        // live data
        movies = new MutableLiveData<>(null);
        votes = new MutableLiveData<>(null);
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
                        snackbarMessage.postValue("Error getting movies.");
                    } else {
                        Log.i(LOG_TAG, "Movies update.");
                        List<Movie> newMovies =
                            querySnapshot != null ? querySnapshot.toObjects(Movie.class) : null;

                        movies.postValue(newMovies);
                        errorMessage.postValue(null);
                        snackbarMessage.postValue("Movies Updated.");
                    }
                });

        // observe the votes collection
        votesRegistration =
            db.collection("movieVote")
                .whereEqualTo("username", username)
                .addSnapshotListener((@NonNull QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e(LOG_TAG, "Error getting votes.", error);
                        snackbarMessage.postValue("Error getting votes.");
                    } else {
                        Log.i(LOG_TAG, "Votes update.");

                        //List<MovieVoteValue> newVotes = querySnapshot.toObjects(MovieVoteValue.class);

                        List<MovieVoteValue> newVotes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String movieId = document.getString("movieId");
                            Long value = document.getLong("value");
                            if (movieId != null && value != null) {
                                newVotes.add(new MovieVoteValue(movieId, value.intValue()));
                            }
                        }

                        votes.postValue(newVotes);
                        // snackbarMessage.postValue("Votes Updated.");
                    }
                });
    }

    @Override
    protected void onCleared() {
        if (moviesRegistration != null) {
            moviesRegistration.remove();
        }
        if (votesRegistration != null) {
            votesRegistration.remove();
        }
        super.onCleared();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<List<MovieVoteValue>> getVotes() {
        return votes;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSnackbarMessage() {
        return snackbarMessage;
    }

    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

    private void vote(String movieId, int value) {
        HashMap<String, Object> vote = new HashMap<>();
        vote.put("movieId", movieId);
        vote.put("username", username);
        vote.put("votedOn", FieldValue.serverTimestamp());
        vote.put("value", value);

        db.collection("movieVote")
            .document(username + ";" + movieId)
            .set(vote)
            .addOnCompleteListener((Task<Void> task) -> {
                if (!task.isSuccessful()) {
                    Log.e(LOG_TAG, "Failed to save vote.", task.getException());
                    snackbarMessage.postValue("Failed to save vote.");
                } else {
                    Log.i(LOG_TAG, "Vote saved.");
                    snackbarMessage.postValue("Vote saved.");
                }
            });
    }

    public void clearVote(String movieId) {
        db.collection("movieVote")
            .document(username + ";" + movieId)
            .delete()
            .addOnCompleteListener((Task<Void> task) -> {
                if (!task.isSuccessful()) {
                    Log.e(LOG_TAG, "Failed to clear vote.", task.getException());
                    snackbarMessage.postValue("Failed to clear vote.");
                } else {
                    Log.i(LOG_TAG, "Vote cleared.");
                    snackbarMessage.postValue("Vote cleared.");
                }
            });
    }

    public void upvote(String movieId) {
        vote(movieId, 1);
    }

    public void downvote(String movieId) {
        vote(movieId, -1);
    }

}
