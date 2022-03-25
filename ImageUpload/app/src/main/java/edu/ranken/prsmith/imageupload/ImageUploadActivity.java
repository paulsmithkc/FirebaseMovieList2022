package edu.ranken.prsmith.imageupload;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ImageUploadActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = ImageUploadActivity.class.getSimpleName();
    private static final String FILE_PROVIDER_AUTHORITY = "edu.ranken.prsmith.imageupload.fileprovider";

    // views
    private ImageView imagePreview;
    private Button cameraButton;
    private Button galleryButton;
    private TextView errorText;
    private TextView downloadUrlText;

    // state
    private File outputImageFile;
    private Uri outputImageUri;

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
    private final ActivityResultLauncher<Uri> takePictureLauncher =
        registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            (Boolean result) -> {
                Log.i(LOG_TAG, "take picture result: " + result);
                if (Objects.equals(result, Boolean.TRUE)) {
                    uploadImage(outputImageUri);
                } else {
                    Log.e(LOG_TAG, "failed to return picture");
                }
            }
        );

    // model
    private ImageUploadViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        // find views
        imagePreview = findViewById(R.id.imagePreview);
        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);
        errorText = findViewById(R.id.errorText);
        downloadUrlText = findViewById(R.id.downloadUrlText);

        // register listeners
        cameraButton.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "camera");
            try {
                outputImageFile = createImageFile();
                Log.i(LOG_TAG, "outputImageFile = " + outputImageFile);
                outputImageUri = fileToUri(outputImageFile);
                Log.i(LOG_TAG, "outputImageUri = " + outputImageUri);
                takePictureLauncher.launch(outputImageUri);
            } catch (Exception ex) {
                Log.e(LOG_TAG, "take picture failed", ex);
            }
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

        // disable the camera if not available
        // boolean hasCamera = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        boolean hasCamera = Camera.getNumberOfCameras() > 0;
        cameraButton.setVisibility(hasCamera ? View.VISIBLE : View.GONE);
        Log.i(LOG_TAG, "hasCamera = " + hasCamera);
    }

    public void uploadImage(Uri uri) {
        Log.i(LOG_TAG, "upload image: " + uri);

        Picasso
            .get()
            .load(uri)
            .resize(400,400)
            .centerCrop()
            .into(imagePreview);

        model.uploadProfileImage(uri);
    }

    private File createImageFile() throws IOException {
        // create file name
        Calendar now = Calendar.getInstance();
        String fileName = String.format(Locale.US, "image_%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS.jpg", now);

        // create paths
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, fileName);

        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e(LOG_TAG, "Failed to create directories: " + storageDir);
            } else {
                Log.e(LOG_TAG, "Directories created: " + storageDir);
            }
        }

//        File sdcard = Environment.getExternalStorageDirectory();
//        if (sdcard != null) {
//            Log.i(LOG_TAG, "sdcard = " + sdcard);
//            File cameraDir = new File(sdcard, "/DCIM/Camera");
//            boolean cameraExists = cameraDir.exists();
//            boolean cameraCreated = cameraDir.mkdirs();
//            Log.i(LOG_TAG, "Directory " + cameraDir + " created: " + (cameraExists || cameraCreated));
//        }

        // return File object
        return imageFile;
    }

//    private Uri fileToUri(File file) {
//        return Uri.fromFile(file);
//    }

    private Uri fileToUri(File file) {
        return FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, file);
    }
}