package edu.ranken.prsmith.movielist2022.ui.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

import edu.ranken.prsmith.movielist2022.R;

public class MyProfileViewModel extends ViewModel {

    // constants
    private static final String LOG_TAG = MyProfileViewModel.class.getSimpleName();

    // misc
    private final FirebaseFirestore db;

    // live data
    private final MutableLiveData<FirebaseUser> user;
    private final MutableLiveData<Integer> userError;
    private final MutableLiveData<Integer> snackbarMessage;

    public MyProfileViewModel() {
        db = FirebaseFirestore.getInstance();

        // live data
        user = new MutableLiveData<>(null);
        userError = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);

        // get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            user.postValue(currentUser);
        } else {
            userError.postValue(R.string.errorCurrentlySignedOut);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    // getters

    public LiveData<FirebaseUser> getUser() {
        return user;
    }
    public MutableLiveData<Integer> getUserError() {
        return userError;
    }
    public MutableLiveData<Integer> getSnackbarMessage() {
        return snackbarMessage;
    }

    // mutators

    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

    public void updateDisplayName(String newDisplayName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(LOG_TAG, "Cannot update display name, because user is not authenticated.");
            snackbarMessage.postValue(R.string.errorCurrentlySignedOut);
        } else {
            UserProfileChangeRequest request =
                new UserProfileChangeRequest.Builder()
                    .setDisplayName(newDisplayName)
                    .build();

            HashMap<String, Object> update = new HashMap<>();
            update.put("displayName", newDisplayName);

            currentUser
                .updateProfile(request)
                .addOnSuccessListener((result) -> {
                    Log.i(LOG_TAG, "Display name updated in auth.");

                    db.collection("users")
                        .document(currentUser.getUid())
                        .set(update, SetOptions.merge())
                        .addOnSuccessListener((result2) -> {
                            Log.e(LOG_TAG, "Display name updated in database.");
                            user.postValue(currentUser);
                            userError.postValue(null);
                            snackbarMessage.postValue(R.string.profileUpdated);
                        })
                        .addOnFailureListener((error2) -> {
                            Log.e(LOG_TAG, "Failed to update display name in database.", error2);
                            snackbarMessage.postValue(R.string.errorUpdateProfile);
                        });

                })
                .addOnFailureListener((error) -> {
                    Log.e(LOG_TAG, "Failed to update display name in auth.", error);
                    snackbarMessage.postValue(R.string.errorUpdateProfile);
                });
        }
    }
}
