package edu.ranken.prsmith.movielist2022.ui.user;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import edu.ranken.prsmith.movielist2022.data.UserProfile;

public class MyProfileViewModel extends ViewModel {

    // constants
    private static final String LOG_TAG = MyProfileViewModel.class.getSimpleName();

    // misc
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private ListenerRegistration userProfileRegistration;

    // live data
    private final MutableLiveData<FirebaseUser> user;
    private final MutableLiveData<UserProfile> userProfile;
    private final MutableLiveData<String> snackbarMessage;

    public MyProfileViewModel() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // live data
        user = new MutableLiveData<>(null);
        userProfile = new MutableLiveData<>();
        snackbarMessage = new MutableLiveData<>(null);

        // get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        user.postValue(currentUser);

        // get current user profile
        if (currentUser == null) {
            Log.e(LOG_TAG, "You are currently signed-out.");
            snackbarMessage.postValue("You are currently signed-out.");
        } else {
            userProfileRegistration =
                db.collection("users")
                    .document(currentUser.getUid())
                    .addSnapshotListener((document, error) -> {
                        if (error != null) {
                            Log.e(LOG_TAG, "Failed to load public profile.", error);
                            snackbarMessage.postValue("Failed to load public profile.");
                        } else if (document != null && document.exists()) {
                            UserProfile newUserProfile = document.toObject(UserProfile.class);
                            userProfile.postValue(newUserProfile);
                        } else {
                            userProfile.postValue(null);
                        }
                    });
        }
    }

    @Override
    protected void onCleared() {
        if (userProfileRegistration != null) {
            userProfileRegistration.remove();
        }
        super.onCleared();
    }

    // getters

    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public LiveData<String> getSnackbarMessage() {
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
            snackbarMessage.postValue("You are currently signed-out.");
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
                            Log.i(LOG_TAG, "Display name updated in database.");
                            snackbarMessage.postValue("Profile updated.");
                        })
                        .addOnFailureListener((error2) -> {
                            Log.e(LOG_TAG, "Failed to update display name in database.", error2);
                            snackbarMessage.postValue("Failed to update profile.");
                        });

                })
                .addOnFailureListener((error) -> {
                    Log.e(LOG_TAG, "Failed to update display name in auth.", error);
                    snackbarMessage.postValue("Failed to update profile.");
                });
        }
    }

    public void uploadProfilePhoto(Uri photoFileUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(LOG_TAG, "Cannot upload profile photo, because user is not authenticated.");
            snackbarMessage.postValue("You are currently signed-out.");
        } else {
            // create a storage reference
            String userId = currentUser.getUid();
            StorageReference storageRef = storage.getReference("/user/" + userId + "/profilePhoto.png");

            // start uploading the file
            UploadTask uploadTask = storageRef.putFile(photoFileUri);

            // perform additional tasks after the file is uploaded
            // FIXME: test that all failure modes are captured and handled
            uploadTask
                .continueWithTask((continuation) -> {
                    Log.i(LOG_TAG, "Profile photo uploaded to: " + storageRef.getPath());
                    return storageRef.getDownloadUrl();
                })
                .continueWithTask((continuation) -> {
                    Uri downloadUrl = continuation.getResult();
                    Log.i(LOG_TAG, "Download URL obtained: " + downloadUrl);

                    UserProfileChangeRequest request =
                        new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build();

                    HashMap<String, Object> update = new HashMap<>();
                    update.put("photoUrl", downloadUrl.toString());

                    Task<Void> task1 = currentUser.updateProfile(request);
                    Task<Void> task2 = task1.continueWithTask((continuation2) ->
                        db.collection("users").document(currentUser.getUid()).set(update, SetOptions.merge())
                    );
                    return task2;
                })
                .addOnSuccessListener((result) -> {
                    Log.i(LOG_TAG, "Profile photo updated.");
                    snackbarMessage.postValue("Profile photo updated.");
                })
                .addOnFailureListener((error) -> {
                    Log.i(LOG_TAG, "Failed to update profile photo.", error);
                    snackbarMessage.postValue("Failed to update profile photo.");
                });

        }
    }
}
