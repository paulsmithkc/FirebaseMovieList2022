package edu.ranken.prsmith.movielist2022;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class UserProfileActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = UserProfileActivity.class.getSimpleName();
    public static final String EXTRA_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
    }
}