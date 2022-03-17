package edu.ranken.prsmith.movielist2022.ui.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

import edu.ranken.prsmith.movielist2022.data.UserProfile;

public class UserListViewModel extends ViewModel {

    // constants
    private static final String LOG_TAG = UserListViewModel.class.getSimpleName();

    // misc
    private final FirebaseFirestore db;
    private ListenerRegistration usersRegistration;

    // live data
    private final MutableLiveData<List<UserProfile>> users;
    private final MutableLiveData<String> usersError;
    private final MutableLiveData<String> snackbarMessage;

    public UserListViewModel() {
        db = FirebaseFirestore.getInstance();

        // live data
        users = new MutableLiveData<>(null);
        usersError = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);

        // query users collection
        queryUsers();
    }

    @Override
    protected void onCleared() {
        if (usersRegistration != null) {
            usersRegistration.remove();
        }
        super.onCleared();
    }

    // getters

    public LiveData<List<UserProfile>> getUsers() {
        return users;
    }
    public LiveData<String> getUsersError() {
        return usersError;
    }
    public MutableLiveData<String> getSnackbarMessage() {
        return snackbarMessage;
    }

    // mutators

    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

    private void queryUsers() {
        if (usersRegistration != null) {
            usersRegistration.remove();
        }

        // create query
        Query query = db.collection("users");

        // sort results by name
        query = query.orderBy("displayName");

        // execute query
        usersRegistration =
            query.addSnapshotListener((querySnapshot, error) -> {
                if (error != null) {
                    Log.e(LOG_TAG, "Failed to get users.", error);
                    usersError.postValue("Failed to get users.");
                } else if (querySnapshot != null) {
                    Log.e(LOG_TAG, "Users updated.");
                    List<UserProfile> newUsers = querySnapshot.toObjects(UserProfile.class);
                    usersError.postValue(null);
                    users.postValue(newUsers);
                }
            });
    }
}
