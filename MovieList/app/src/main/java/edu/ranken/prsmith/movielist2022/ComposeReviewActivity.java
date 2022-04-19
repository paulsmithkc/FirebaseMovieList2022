package edu.ranken.prsmith.movielist2022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class ComposeReviewActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = ComposeReviewActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE_ID = "movieId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_compose);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        // outState.putString("movieId", movieId);
        // outState.putParcelable("text", text);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as back navigation
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}