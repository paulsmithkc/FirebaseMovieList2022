package edu.ranken.prsmith.imageupload;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // views
    private ImageView imagePreview;
    private Button cameraButton;
    private Button galleryButton;
    private TextView errorText;
    private TextView downloadUrlText;

    // launchers
    private final ActivityResultLauncher<String> getContentLauncher =
        registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            (Uri uri) -> {
                if (uri != null) {
                    uploadImage(uri);
                }
            }
        );

    // model
    private ImageUploadViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        imagePreview = findViewById(R.id.imagePreview);
        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);
        errorText = findViewById(R.id.errorText);
        downloadUrlText = findViewById(R.id.downloadUrlText);

        // register listeners
        cameraButton.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "camera");
        });
        galleryButton.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "gallery");

//            Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            getContentIntent.setType("image/*");
//            Intent chooserIntent = Intent.createChooser(getContentIntent, "Upload Image");
//            startActivity(chooserIntent);

            getContentLauncher.launch("image/*");
        });

        // bind model
        model = new ViewModelProvider(this).get(ImageUploadViewModel.class);
        model.getUploadErrorMessage().observe(this, (message) -> {
            errorText.setText(message);
        });
        model.getDownloadUrl().observe(this, (downloadUrl) -> {
            if (downloadUrl != null) {
                downloadUrlText.setText(downloadUrl.toString());
            } else {
                downloadUrlText.setText("");
            }
        });
    }

    public void uploadImage(Uri uri) {
        Log.i(LOG_TAG, "user picked: " + uri);
        model.uploadProfileImage(uri);
    }
}