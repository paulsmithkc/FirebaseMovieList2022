package edu.ranken.prsmith.movielist2022;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import edu.ranken.prsmith.movielist2022.ui.user.MyProfileViewModel;

public class MyProfileActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = MyProfileActivity.class.getSimpleName();

    // views
    private ImageView image;
    private TextView id;
    private TextView displayName;
    private TextView email;
    private TextView emailVerified;

    // state
    private MyProfileViewModel model;

    // colors
    private int successColor;
    private int errorColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // find views
        image = findViewById(R.id.myProfileImage);
        id = findViewById(R.id.myProfileId);
        displayName = findViewById(R.id.myProfileDisplayName);
        email = findViewById(R.id.myProfileEmail);
        emailVerified = findViewById(R.id.myProfileEmailVerified);

        // get colors
        successColor = getColor(R.color.success);
        errorColor = getColor(R.color.error);

        // bind model
        model = new ViewModelProvider(this).get(MyProfileViewModel.class);
        model.getSnackbarMessage().observe(this, (String snackbarMessage) -> {
            if (snackbarMessage != null) {
                //Snackbar.make(id, snackbarMessage, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            }
        });
        model.getUser().observe(this, (FirebaseUser user) -> {
            if (user == null || user.getPhotoUrl() == null) {
                image.setImageResource(R.drawable.ic_broken_image);
            } else {
                image.setImageResource(R.drawable.ic_downloading);
            }

            id.setText(user == null ? "" : user.getUid());
            displayName.setText(user == null ? "" : user.getDisplayName());
            email.setText(user == null ? "" : user.getEmail());

            if (user == null) {
                emailVerified.setText("");
            } else if (user.isEmailVerified()) {
                emailVerified.setText(R.string.emailVerified);
                emailVerified.setTextColor(successColor);
            } else {
                emailVerified.setText(R.string.emailUnverified);
                emailVerified.setTextColor(errorColor);
            }
        });
    }
}