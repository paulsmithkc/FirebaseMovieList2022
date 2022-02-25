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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ranken.prsmith.movielist2022.data.Genre;
import edu.ranken.prsmith.movielist2022.data.Movie;
import edu.ranken.prsmith.movielist2022.data.MovieVoteValue;

public class MovieListViewModel extends ViewModel {
    private static final String LOG_TAG = "MovieListViewModel";

    private final FirebaseFirestore db;
    private ListenerRegistration moviesRegistration;
    private ListenerRegistration votesRegistration;
    private ListenerRegistration genresRegistration;
    private String username = "prsmith";  // FIXME: implement a login screen, and use the logged in user's id
    private String filterGenreId = null;

    // live data
    private final MutableLiveData<List<Movie>> movies;
    private final MutableLiveData<List<MovieVoteValue>> votes;
    private final MutableLiveData<List<Genre>> genres;
    private final MutableLiveData<String> errorMessage;  // FIXME: what if there are multiple errors at once??
    private final MutableLiveData<String> snackbarMessage;

    public MovieListViewModel() {
        db = FirebaseFirestore.getInstance();

        // live data
        movies = new MutableLiveData<>(null);
        votes = new MutableLiveData<>(null);
        genres = new MutableLiveData<>(null);
        errorMessage = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);

        // observe movies collection
        queryMovies();

        // observe the votes collection
        // FIXME: move to a separate method
        // FIXME: extract user-visible strings
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

        // observe genres collection
        // FIXME: move to a separate method
        // FIXME: extract user-visible strings
        // FIXME: sort by name
        genresRegistration =
            db.collection("genres")
              .addSnapshotListener((@NonNull QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                  if (error != null) {
                      Log.e(LOG_TAG, "Error getting genres.", error);
                      snackbarMessage.postValue("Error getting genres.");
                  } else {
                      Log.i(LOG_TAG, "Genres updated.");
                      List<Genre> newGenres = querySnapshot.toObjects(Genre.class);
                      genres.postValue(newGenres);
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
        if (genresRegistration != null) {
            genresRegistration.remove();
        }
        super.onCleared();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<List<MovieVoteValue>> getVotes() {
        return votes;
    }

    public LiveData<List<Genre>> getGenres() {
        return genres;
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

    private void addVoteForMovie(String movieId, int value) {
        HashMap<String, Object> vote = new HashMap<>();
        vote.put("movieId", movieId);
        vote.put("username", username);
        vote.put("votedOn", FieldValue.serverTimestamp());
        vote.put("value", value);

        // FIXME: extract user-visible strings
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

    public void removeVoteFromMovie(String movieId) {
        // FIXME: extract user-visible strings
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

    public void addUpvoteForMovie(String movieId) {
        addVoteForMovie(movieId, 1);
    }

    public void addDownvoteForMovie(String movieId) {
        addVoteForMovie(movieId, -1);
    }

    public void filterMoviesByGenre(String genreId) {
        this.filterGenreId = genreId;
        queryMovies();
    }

    private void queryMovies() {
        if (moviesRegistration != null) {
            moviesRegistration.remove();
        }

        // FIXME: sort movies by: name, releaseYear
        // FIXME: filter down to personal lists
        Query query = db.collection("movies");
        if (filterGenreId != null) {
            query = query.whereEqualTo("genre." + filterGenreId, true);
        }

        // FIXME: extract user-visible strings
        moviesRegistration =
            query.addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
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
    }

}
