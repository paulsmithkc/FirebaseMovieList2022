package edu.ranken.prsmith.movielist2022;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    // views
    private Button loginButton;

    // state
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find views
        loginButton = findViewById(R.id.loginButton);

        // Register a callback for when the sign in process is complete
        signInLauncher =
            registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                (result) -> onSignInResult(result)
            );

        // register listeners
        loginButton.setOnClickListener((view) -> {

            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = new ArrayList<>();
            providers.add(new AuthUI.IdpConfig.EmailBuilder().build());
            providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create sign-in intent
            Intent signInIntent =
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();

            // Launch sign-in activity
            signInLauncher.launch(signInIntent);

        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            onLoginSuccess(user);
        } else {
            loginButton.performClick();
        }
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.i(LOG_TAG, "sign-in successful: " + user.getUid());
            onLoginSuccess(user);
        } else {
            IdpResponse response = result.getIdpResponse();
            if (response != null) {
                FirebaseUiException error = response.getError();
                Log.e(LOG_TAG, "sign-in failed", error);
            } else {
                Log.e(LOG_TAG, "sign-in failed: no response");
            }
        }
    }

    private void onLoginSuccess(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userId = user.getUid();
        String displayName = user.getDisplayName();
        Uri photoUrl = user.getPhotoUrl();
        String photoUrlString = photoUrl != null ? photoUrl.toString() : null;

        HashMap<String, Object> update = new HashMap<>();
        update.put("displayName", displayName);
        update.put("photoUrl", photoUrlString);
        update.put("lastLogin", FieldValue.serverTimestamp());

        db.collection("users")
            .document(userId)
            .set(update, SetOptions.merge())
            .addOnSuccessListener((result) -> {
                Log.e(LOG_TAG, "User profile updated.");
                startActivity(new Intent(this, MovieListActivity.class));
            })
            .addOnFailureListener((error) -> {
                Log.e(LOG_TAG, "Failed to update user profile on login.", error);
                startActivity(new Intent(this, MovieListActivity.class));
            });
    }
}