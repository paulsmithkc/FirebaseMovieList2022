package edu.ranken.prsmith.movielist2022.ui.movie;

import android.util.Log;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ranken.prsmith.movielist2022.R;
import edu.ranken.prsmith.movielist2022.data.Genre;
import edu.ranken.prsmith.movielist2022.data.Movie;
import edu.ranken.prsmith.movielist2022.data.MovieList;
import edu.ranken.prsmith.movielist2022.data.MovieSummary;
import edu.ranken.prsmith.movielist2022.data.MovieVote;
import edu.ranken.prsmith.movielist2022.data.MovieVoteValue;
import edu.ranken.prsmith.movielist2022.ui.utils.ErrorMessageContainer;
import edu.ranken.prsmith.movielist2022.ui.utils.SnackbarMessageContainer;

public class MovieListViewModel extends ViewModel {
    private static final String LOG_TAG = MovieListViewModel.class.getSimpleName();

    private final FirebaseFirestore db;
    private ListenerRegistration moviesRegistration;
    private ListenerRegistration votesRegistration;
    private ListenerRegistration genresRegistration;
    private String userId;
    private String filterGenreId = null;
    private MovieList filterList = MovieList.ALL_MOVIES;

    // live data
    private final MutableLiveData<List<MovieSummary>> movies;
    private final MutableLiveData<List<MovieVoteValue>> votes;
    private final MutableLiveData<List<Genre>> genres;
    private final ErrorMessageContainer errorMessages;
    private final SnackbarMessageContainer snackbarMessages;

    public MovieListViewModel() {
        db = FirebaseFirestore.getInstance();

        // live data
        movies = new MutableLiveData<>(null);
        votes = new MutableLiveData<>(null);
        genres = new MutableLiveData<>(null);
        errorMessages = new ErrorMessageContainer();
        snackbarMessages = new SnackbarMessageContainer();

        // get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            userId = null;
        }

        // observe collections
        queryMovies();
        queryVotes();
        queryGenres();
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

    public ErrorMessageContainer getErrorMessages() {
        return errorMessages;
    }

    public SnackbarMessageContainer getSnackbarMessages() {
        return snackbarMessages;
    }

    public String getFilterGenreId() {
        return filterGenreId;
    }

    public void addUpvoteForMovie(MovieSummary movie) {
        addVoteForMovie(movie, 1);
    }

    public void addDownvoteForMovie(MovieSummary movie) {
        addVoteForMovie(movie, -1);
    }

    private void addVoteForMovie(MovieSummary movie, int value) {
        HashMap<String, Object> vote = new HashMap<>();
        vote.put("movieId", movie.id);
        vote.put("userId", userId);
        vote.put("votedOn", FieldValue.serverTimestamp());
        vote.put("value", value);
        vote.put("movie", movie);

        db.collection("movieVote")
            .document(userId + ";" + movie.id)
            .set(vote)
            .addOnCompleteListener((Task<Void> task) -> {
                if (!task.isSuccessful()) {
                    Log.e(LOG_TAG, "Failed to save vote.", task.getException());
                    snackbarMessages.addMessage(R.string.errorSaveVote);
                } else {
                    Log.i(LOG_TAG, "Vote saved.");
                    snackbarMessages.addMessage(R.string.voteSaved);
                }
            });
    }

    public void removeVoteFromMovie(String movieId) {
        db.collection("movieVote")
            .document(userId + ";" + movieId)
            .delete()
            .addOnCompleteListener((Task<Void> task) -> {
                if (!task.isSuccessful()) {
                    Log.e(LOG_TAG, "Failed to clear vote.", task.getException());
                    snackbarMessages.addMessage(R.string.errorRemoveVote);
                } else {
                    Log.i(LOG_TAG, "Vote cleared.");
                    snackbarMessages.addMessage(R.string.voteRemoved);
                }
            });
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
                        .whereEqualTo("userId", userId);
                break;
            case MY_UPVOTES:
                query =
                    db.collection("movieVote")
                        .whereEqualTo("userId", userId)
                        .whereGreaterThan("value", 0);
                break;
            case MY_DOWNVOTES:
                query =
                    db.collection("movieVote")
                        .whereEqualTo("userId", userId)
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

        // FIXME: sort movies by: name, releaseYear
        // query = query.orderBy("name").orderBy("releaseYear");

        // FIXME: extract user-visible strings
        moviesRegistration =
            query.addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                if (error != null) {
                    Log.e(LOG_TAG, "Error getting movies.", error);
                    errorMessages.setMessage("movies", R.string.errorFetchingMovies);
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

                    errorMessages.setMessage("movies", null);
                    snackbarMessages.addMessage(R.string.moviesUpdated);
                    movies.postValue(newMovieSummaries);
                }
            });
    }

    private void queryVotes() {
        if (votesRegistration != null) {
            votesRegistration.remove();
        }

        votesRegistration =
            db.collection("movieVote")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e(LOG_TAG, "Error getting votes.", error);
                        errorMessages.setMessage("movieVote", R.string.errorFetchingVotes);
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

                        //snackbarMessages.addMessage(R.string.votesUpdated);
                        errorMessages.setMessage("movieVote", null);
                        votes.postValue(newVotes);
                    }
                });
    }

    private void queryGenres() {
        if (genresRegistration != null) {
            genresRegistration.remove();
        }

        genresRegistration =
            db.collection("genres")
                .orderBy("name")
                .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e(LOG_TAG, "Error getting genres.", error);
                        errorMessages.setMessage("genres", R.string.errorFetchingGenres);
                    } else if (querySnapshot != null) {
                        Log.i(LOG_TAG, "Genres updated.");
                        List<Genre> newGenres = querySnapshot.toObjects(Genre.class);

                        //snackbarMessages.addMessage(R.string.genresUpdated);
                        errorMessages.setMessage("genres", null);
                        genres.postValue(newGenres);
                    }
                });
    }
}
