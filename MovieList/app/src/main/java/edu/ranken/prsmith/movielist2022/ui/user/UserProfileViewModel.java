package edu.ranken.prsmith.movielist2022.ui.user;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

import edu.ranken.prsmith.movielist2022.ui.movie.MovieDetailsViewModel;

public class UserProfileViewModel extends ViewModel {

    // constants
    private static final String LOG_TAG = MovieDetailsViewModel.class.getSimpleName();

    // firebase
    private final FirebaseFirestore db;

    // data
    private String userId;

    // live data

    public UserProfileViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    // fetch a particular user from the database
    public void fetchUser(String userId) {
        this.userId = userId;
    }
}
