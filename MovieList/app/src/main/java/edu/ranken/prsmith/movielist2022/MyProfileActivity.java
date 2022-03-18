package edu.ranken.prsmith.movielist2022;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import edu.ranken.prsmith.movielist2022.ui.user.MyProfileViewModel;

public class MyProfileActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = MyProfileActivity.class.getSimpleName();

    // views
    private ImageView imageView;
    private TextView idView;
    private TextView displayNameView;
    private TextView emailView;
    private TextView emailVerifiedView;

    // state
    private MyProfileViewModel model;
    private Picasso picasso;

    // colors
    private int successColor;
    private int errorColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // find views
        imageView = findViewById(R.id.myProfileImage);
        idView = findViewById(R.id.myProfileId);
        displayNameView = findViewById(R.id.myProfileDisplayName);
        emailView = findViewById(R.id.myProfileEmail);
        emailVerifiedView = findViewById(R.id.myProfileEmailVerified);

        // get colors
        successColor = getColor(R.color.success);
        errorColor = getColor(R.color.error);

        // get picasso
        picasso = Picasso.get();

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
                imageView.setImageResource(R.drawable.ic_broken_image);
            } else {
                imageView.setImageResource(R.drawable.ic_downloading);
                picasso
                    .load(user.getPhotoUrl())
                    .noPlaceholder()
                    //.placeholder(R.drawable.ic_downloading)
                    .error(R.drawable.ic_error)
                    .resize(200, 200)
                    .centerInside()
                    .into(imageView);
            }

            idView.setText(user == null ? "" : user.getUid());
            displayNameView.setText(user == null ? "" : user.getDisplayName());
            emailView.setText(user == null ? "" : user.getEmail());

            if (user == null) {
                emailVerifiedView.setText("");
            } else if (user.isEmailVerified()) {
                emailVerifiedView.setText(R.string.emailVerified);
                emailVerifiedView.setTextColor(successColor);
            } else {
                emailVerifiedView.setText(R.string.emailUnverified);
                emailVerifiedView.setTextColor(errorColor);
            }
        });

        // register listeners
        imageView.setOnClickListener((view) -> {
            // try to upload ic_comedy.png, to test the logic
            // FIXME: allow the user to capture an image with the camera
            // FIXME: allow the user to pick an image from the gallery
            Resources resources = getResources();
            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_comedy, null);
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            model.uploadProfilePhoto(bitmap);
        });
    }
}