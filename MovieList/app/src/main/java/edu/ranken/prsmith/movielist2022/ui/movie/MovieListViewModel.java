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
    private final MutableLiveData<Map<String, Integer>> errorMessages;
    private final MutableLiveData<List<Integer>> snackbarMessages;

    public MovieListViewModel() {
        db = FirebaseFirestore.getInstance();

        // live data
        movies = new MutableLiveData<>(null);
        votes = new MutableLiveData<>(null);
        genres = new MutableLiveData<>(null);
        errorMessages = new MutableLiveData<>(null);
        snackbarMessages = new MutableLiveData<>(null);

        // get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            userId = null;
        }

        // observe movies collection
        queryMovies();

        // observe the votes collection
        // FIXME: move to a separate method
        votesRegistration =
            db.collection("movieVote")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e(LOG_TAG, "Error getting votes.", error);
                        setErrorMessage("movieVote", R.string.errorFetchingVotes);
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

                        setErrorMessage("movieVote", null);
                        votes.postValue(newVotes);
                    }
                });

        // observe genres collection
        // FIXME: move to a separate method
        genresRegistration =
            db.collection("genres")
              .orderBy("name")
              .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                  if (error != null) {
                      Log.e(LOG_TAG, "Error getting genres.", error);
                      setErrorMessage("genres", R.string.errorFetchingGenres);
                  } else if (querySnapshot != null) {
                      Log.i(LOG_TAG, "Genres updated.");
                      List<Genre> newGenres = querySnapshot.toObjects(Genre.class);

                      setErrorMessage("genres", null);
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

    public LiveData<Map<String, Integer>> getErrorMessages() {
        return errorMessages;
    }

    public LiveData<List<Integer>> getSnackbarMessages() {
        return snackbarMessages;
    }

    public String getFilterGenreId() {
        return filterGenreId;
    }

    public void addSnackbar(@StringRes Integer messageId) {
        List<Integer> messages = snackbarMessages.getValue();
        if (messages == null) { messages = new ArrayList<>(); }
        messages.add(messageId);
        snackbarMessages.postValue(messages);
    }

    public void removeSnackbar() {
        List<Integer> messages = snackbarMessages.getValue();
        if (messages != null && messages.size() > 0) {
            messages.remove(0);
            snackbarMessages.postValue(messages);
        }
    }

    public void setErrorMessage(String key, @StringRes Integer messageId) {
        Map<String, Integer> messages = errorMessages.getValue();
        if (messages == null) { messages = new HashMap<>(); }
        messages.put(key, messageId);
        errorMessages.postValue(messages);
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
                    addSnackbar(R.string.errorSaveVote);
                } else {
                    Log.i(LOG_TAG, "Vote saved.");
                    addSnackbar(R.string.voteSaved);
                }
            });
    }

    public void removeVoteFromMovie(String movieId) {
        // FIXME: extract user-visible strings
        db.collection("movieVote")
            .document(userId + ";" + movieId)
            .delete()
            .addOnCompleteListener((Task<Void> task) -> {
                if (!task.isSuccessful()) {
                    Log.e(LOG_TAG, "Failed to clear vote.", task.getException());
                    addSnackbar(R.string.errorRemoveVote);
                } else {
                    Log.i(LOG_TAG, "Vote cleared.");
                    addSnackbar(R.string.voteRemoved);
                }
            });
    }

//    public void deleteAllMyVotes() {
//        db.collection("movieVote")
//            .whereEqualTo("userId", userId)
//            .get()
//            .addOnSuccessListener((querySnapshot) -> {
//                WriteBatch batch = db.batch();
//                for (DocumentSnapshot doc : querySnapshot) {
//                    batch.delete(doc.getReference());
//                }
//
//                batch
//                    .commit()
//                    .addOnSuccessListener((result) -> {
//                        Log.i(LOG_TAG, "All my votes have been deleted.");
//                        snackbarMessage.postValue("All my votes have been deleted.");
//                    })
//                    .addOnFailureListener((error) -> {
//                        Log.e(LOG_TAG, "Failed to delete all my votes.", error);
//                        snackbarMessage.postValue("Failed to delete all my votes.");
//                    });
//            })
//            .addOnFailureListener((error) -> {
//                Log.e(LOG_TAG, "Failed to get all my votes.", error);
//                snackbarMessage.postValue("Failed to get all my votes.");
//            });
//    }

//    public void removeVotesFromSelectedMovies(List<String> movieIds) {
//        // create a batch
//        WriteBatch batch = db.batch();
//
//        // queue selected movies for deletion
//        CollectionReference collection = db.collection("movieVote");
//        for (String movieId : movieIds) {
//            batch.delete(collection.document(userId + ";" + movieId));
//        }
//
//        // commit the batch
//        batch.commit()
//            .addOnSuccessListener((result) -> {
//                Log.i(LOG_TAG, "Selected votes have been deleted.");
//                snackbarMessage.postValue("Selected votes have been deleted.");
//            })
//            .addOnFailureListener((error) -> {
//                Log.e(LOG_TAG, "Failed to delete selected votes.", error);
//                snackbarMessage.postValue("Failed to delete selected votes.");
//            });
//    }

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
                    setErrorMessage("movies", R.string.errorFetchingMovies);
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

                    setErrorMessage("movies", null);
                    addSnackbar(R.string.moviesUpdated);
                }
            });
    }

}
