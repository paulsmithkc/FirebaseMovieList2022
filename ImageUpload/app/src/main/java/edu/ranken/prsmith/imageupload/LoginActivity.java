package edu.ranken.prsmith.imageupload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

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
    private final ActivityResultLauncher<Intent> signInLauncher =
        registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            (result) -> onSignInResult(result)
        );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find views
        loginButton = findViewById(R.id.loginButton);

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

        // auto-login
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
        startActivity(new Intent(this, MainActivity.class));
    }
}