package edu.ranken.prsmith.movielist2022;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

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
    private Uri uploadProfilePhotoUri;

    // colors
    private int successColor;
    private int errorColor;

    private final ActivityResultLauncher<String> getContentLauncher =
        registerForActivityResult(new ActivityResultContracts.GetContent(),
            (Uri result) -> {
                // Handle the returned Uri
                Log.i(LOG_TAG, "get content: " + result);
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
        registerForActivityResult(new ActivityResultContracts.TakePicture(),
            (Boolean result) -> {
                // Check if an image file was saved
                Log.i(LOG_TAG, "take picture: " + result);
                if (Objects.equals(result, Boolean.TRUE)) {
                    uploadProfilePhoto();
                }
            });

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
//            Resources resources = getResources();
//            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_comedy, null);
//            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
//            model.uploadProfilePhoto(bitmap);

//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            Uri outputUri = null;
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
//            startActivity(takePictureIntent);

//            Intent getImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            getImageIntent.setType("image/*");
//            Intent chooserIntent = Intent.createChooser(getImageIntent, "Upload Image");
//            startActivity(chooserIntent);

            //getContentLauncher.launch("image/*");

            try {
                Uri outputUri = createImageFile();
                Log.i(LOG_TAG, "outputUri: " + outputUri);
                uploadProfilePhotoUri = outputUri;
                takePictureLauncher.launch(outputUri);
            } catch (Exception ex) {
                Log.e(LOG_TAG, "failed to take picture", ex);
            }
        });
    }

    private static final String FILE_PROVIDER_AUTHORITY = "edu.ranken.prsmith.movielist2022.fileprovider";

    private Uri createImageFile() throws IOException {
        // create file name
        Calendar now = Calendar.getInstance();
        String fileName = String.format(Locale.US, "image_%1$tY%1$tm%1$td_%1$tH%1$tM.jpg", now);

        // create paths
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, fileName);

        // create the pictures directory, if it does not exist yet
        imageFile.mkdirs();

        // create the file, if it does not exist yet
        imageFile.createNewFile();

        // create a URI for the file
        Uri imageUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, imageFile);
        return imageUri;
    }

    private void uploadProfilePhoto() {
        model.uploadProfilePhoto(uploadProfilePhotoUri);
    }
}