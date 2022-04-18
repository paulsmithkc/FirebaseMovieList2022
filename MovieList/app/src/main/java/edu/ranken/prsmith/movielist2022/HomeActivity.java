package edu.ranken.prsmith.movielist2022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.ranken.prsmith.movielist2022.ui.home.HomePageAdapter;

public class HomeActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = HomeActivity.class.getSimpleName();

    // views
    private ViewPager2 pager;
    private BottomNavigationView bottomNav;

    // state
    private HomePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // find views
        pager = findViewById(R.id.homePager);
        bottomNav = findViewById(R.id.homeBottomNav);

        // create adapter
        adapter = new HomePageAdapter(this);
        pager.setAdapter(adapter);

        // register listener
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNav.getMenu().getItem(position).setChecked(true);
            }
        });
        bottomNav.setOnItemSelectedListener((MenuItem item) -> {
            int itemId = item.getItemId();
            if (itemId == R.id.actionMovieList) {
                pager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.actionUserList) {
                pager.setCurrentItem(1);
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as back navigation
            onBackPressed();
            return true;
        } else if (itemId == R.id.actionSignOut) {
            onSignOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.i(LOG_TAG, "Back Pressed");
    }

    public void onSignOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener((result) -> {
                Log.i(LOG_TAG, "Signed out.");
                finish();
            });
    }
}