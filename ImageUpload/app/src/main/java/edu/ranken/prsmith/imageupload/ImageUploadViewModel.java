package edu.ranken.prsmith.imageupload;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageUploadViewModel extends ViewModel {

    // constants
    private static final String LOG_TAG = ImageUploadViewModel.class.getSimpleName();

    // firebase
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseUser user;

    // live data
    private MutableLiveData<String> uploadErrorMessage;
    private MutableLiveData<Uri> downloadUrl;

    public ImageUploadViewModel() {
        // firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // live data
        uploadErrorMessage = new MutableLiveData<>();
        downloadUrl = new MutableLiveData<>();
    }

    public LiveData<String> getUploadErrorMessage() {
        return uploadErrorMessage;
    }

    public LiveData<Uri> getDownloadUrl() {
        return downloadUrl;
    }

    public void uploadProfileImage(Uri profileImageUri) {
        // 1. Create a StorageReference
        // 2. Upload file using putFile()
        // 3. Update metadata
        // 4. Get download URL
        // 5. Update profile photo in FirebaseUser
        // 6. Update user document in firestore database

        String userId = user.getUid();
        StorageReference storageRef =
            storage.getReference("/user/" + userId + "/profilePhoto");

        Log.i(LOG_TAG, "Uploading " + profileImageUri + " to storage " + storageRef.getPath());
        uploadErrorMessage.postValue("Uploading...");
        downloadUrl.postValue(null);

        storageRef
            .putFile(profileImageUri)
            .addOnCompleteListener((task) -> {
                if (!task.isSuccessful()) {
                    Log.e(LOG_TAG, "failed to upload image to: " + storageRef.getPath(), task.getException());
                    uploadErrorMessage.postValue("Failed to upload.");
                } else {
                    Log.i(LOG_TAG, "image uploaded to: " + storageRef.getPath());
                    getProfileImageDownloadUrl(storageRef);
                }
            });
    }

    private void getProfileImageDownloadUrl(StorageReference storageRef) {
        storageRef
            .getDownloadUrl()
            .addOnCompleteListener((downloadTask) -> {
                if (!downloadTask.isSuccessful()) {
                    Log.e(LOG_TAG, "failed to get download url for: " + storageRef.getPath(), downloadTask.getException());
                    uploadErrorMessage.postValue("Failed to get download URL.");
                } else {
                    Uri downloadUrl = downloadTask.getResult();
                    Log.i(LOG_TAG, "download url: " + downloadUrl);
                    this.uploadErrorMessage.postValue(null);
                    this.downloadUrl.postValue(downloadUrl);

                    // update auth database ...
                    // update firestore database ...
                }
            });
    }
}
