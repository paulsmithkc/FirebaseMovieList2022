package edu.ranken.prsmith.movielist2022.ui;

import android.util.Log;

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

import edu.ranken.prsmith.movielist2022.R;
import edu.ranken.prsmith.movielist2022.data.Genre;
import edu.ranken.prsmith.movielist2022.data.Movie;
import edu.ranken.prsmith.movielist2022.data.MovieList;
import edu.ranken.prsmith.movielist2022.data.MovieSummary;
import edu.ranken.prsmith.movielist2022.data.MovieVote;
import edu.ranken.prsmith.movielist2022.data.MovieVoteValue;

public class MovieListViewModel extends ViewModel {
    private static final String LOG_TAG = "MovieListViewModel";

    private final FirebaseFirestore db;
    private ListenerRegistration moviesRegistration;
    private ListenerRegistration votesRegistration;
    private ListenerRegistration genresRegistration;
    private String username = "prsmith";  // FIXME: implement a login screen, and use the logged in user's id
    private String filterGenreId = null;
    private MovieList filterList = MovieList.ALL_MOVIES;

    // live data
    private final MutableLiveData<List<MovieSummary>> movies;
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
                .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e(LOG_TAG, "Error getting votes.", error);
                        snackbarMessage.postValue("Error getting votes.");
                    } else if (querySnapshot != null) {
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
        genresRegistration =
            db.collection("genres")
              .orderBy("name")
              .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                  if (error != null) {
                      Log.e(LOG_TAG, "Error getting genres.", error);
                      snackbarMessage.postValue("Error getting genres.");
                  } else if (querySnapshot != null) {
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

    public LiveData<List<MovieSummary>> getMovies() {
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

    public String getFilterGenreId() {
        return filterGenreId;
    }

    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

    private void addVoteForMovie(MovieSummary movie, int value) {
        HashMap<String, Object> vote = new HashMap<>();
        vote.put("movieId", movie.id);
        vote.put("username", username);
        vote.put("votedOn", FieldValue.serverTimestamp());
        vote.put("value", value);
        vote.put("movie", movie);

        // FIXME: extract user-visible strings
        db.collection("movieVote")
            .document(username + ";" + movie.id)
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

    public void addUpvoteForMovie(MovieSummary movie) {
        addVoteForMovie(movie, 1);
    }

    public void addDownvoteForMovie(MovieSummary movie) {
        addVoteForMovie(movie, -1);
    }

    public void filterMoviesByGenre(String genreId) {
        this.filterGenreId = genreId;
        queryMovies();
    }

    public void filterMoviesByList(MovieList list) {
        this.filterList = list;
        queryMovies();
    }

    private void queryMovies() {
        if (moviesRegistration != null) {
            moviesRegistration.remove();
        }

        // FIXME: sort movies by: name, releaseYear

        Query query;
        switch (filterList) {
            default:
                throw new IllegalStateException("Unsupported Option");
            case ALL_MOVIES:
                query = db.collection("movies");
                break;
            case MY_VOTES:
                query =
                    db.collection("movieVote")
                        .whereEqualTo("username", username);
                break;
            case MY_UPVOTES:
                query =
                    db.collection("movieVote")
                        .whereEqualTo("username", username)
                        .whereGreaterThan("value", 0);
                break;
            case MY_DOWNVOTES:
                query =
                    db.collection("movieVote")
                        .whereEqualTo("username", username)
                        .whereLessThan("value", 0);
                break;
        }

        if (filterGenreId != null) {
            if (filterList == MovieList.ALL_MOVIES) {
                query = query.whereEqualTo("genre." + filterGenreId, true);
            } else {
                query = query.whereEqualTo("movie.genre." + filterGenreId, true);
            }
        }

        // FIXME: extract user-visible strings
        moviesRegistration =
            query.addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                if (error != null) {
                    Log.e(LOG_TAG, "Error getting movies.", error);
                    errorMessage.postValue(error.getMessage());
                    snackbarMessage.postValue("Error getting movies.");
                } else if (querySnapshot != null) {
                    Log.i(LOG_TAG, "Movies update.");

                    ArrayList<MovieSummary> newMovieSummaries = new ArrayList<>();
                    switch (filterList) {
                        default:
                            throw new IllegalStateException("Unsupported Option");
                        case ALL_MOVIES:
                            List<Movie> newMovies = querySnapshot.toObjects(Movie.class);
                            for (Movie movie : newMovies) {
                                newMovieSummaries.add(new MovieSummary(movie));
                            }
                            break;
                        case MY_VOTES:
                        case MY_UPVOTES:
                        case MY_DOWNVOTES:
                            List<MovieVote> newVotes = querySnapshot.toObjects(MovieVote.class);
                            for (MovieVote vote : newVotes) {
                                if (vote.movie != null) {
                                    // vote.movie.id = vote.movieId;
                                    newMovieSummaries.add(vote.movie);
                                }
                            }
                            break;
                    }
                    movies.postValue(newMovieSummaries);

                    errorMessage.postValue(null);
                    snackbarMessage.postValue("Movies Updated.");
                }
            });
    }

}
