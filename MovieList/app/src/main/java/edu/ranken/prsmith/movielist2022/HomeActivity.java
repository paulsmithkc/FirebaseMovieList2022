package edu.ranken.prsmith.movielist2022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.ranken.prsmith.movielist2022.ui.home.HomePageAdapter;
import edu.ranken.prsmith.movielist2022.ui.movie.MovieDetailsFragment;
import edu.ranken.prsmith.movielist2022.ui.movie.MovieDetailsViewModel;
import edu.ranken.prsmith.movielist2022.ui.movie.MovieListViewModel;
import edu.ranken.prsmith.movielist2022.ui.user.UserListViewModel;
import edu.ranken.prsmith.movielist2022.ui.user.UserProfileFragment;
import edu.ranken.prsmith.movielist2022.ui.user.UserProfileViewModel;

public class HomeActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = HomeActivity.class.getSimpleName();

    // views
    private ViewPager2 pager;
    private BottomNavigationView bottomNav;
    private FragmentContainerView detailsContainer;

    // state
    private HomePageAdapter adapter;
    private MovieListViewModel movieListModel;
    private MovieDetailsViewModel movieDetailsViewModel;
    private UserListViewModel userListModel;
    private UserProfileViewModel userProfileModel;
    private String movieId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // find views
        pager = findViewById(R.id.homePager);
        bottomNav = findViewById(R.id.homeBottomNav);
        detailsContainer = findViewById(R.id.homeDetailsContainer);

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

        // get models
        movieListModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        movieDetailsViewModel = new ViewModelProvider(this).get(MovieDetailsViewModel.class);
        userListModel = new ViewModelProvider(this).get(UserListViewModel.class);
        userProfileModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        // observe models
        movieListModel.getSelectedMovie().observe(this, (movie) -> {
            movieId = movie != null ? movie.id : null;

            if (detailsContainer == null) {
                if (movie != null) {
                    movieListModel.setSelectedMovie(null);

                    Intent intent = new Intent(this, MovieDetailsActivity.class);
                    intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE_ID, movie.id);
                    this.startActivity(intent);
                }
            } else {
                if (movie != null) {
                    movieDetailsViewModel.fetchMovie(movie.id);
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeDetailsContainer, MovieDetailsFragment.class, null)
                        .commit();
                } else {
                    movieDetailsViewModel.fetchMovie(null);
                }
            }
        });
        userListModel.getSelectedUser().observe(this, (user) -> {
            userId = user != null ? user.userId : null;

            if (detailsContainer == null) {
                if (user != null) {
                    userListModel.setSelectedUser(null);

                    Intent intent = new Intent(this, UserProfileActivity.class);
                    intent.putExtra(UserProfileActivity.EXTRA_USER_ID, user.userId);
                    this.startActivity(intent);
                }
            } else {
                if (user != null) {
                    userProfileModel.fetchUser(user.userId);
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeDetailsContainer, UserProfileFragment.class, null)
                        .commit();
                } else {
                    userProfileModel.fetchUser(null);
                }
            }
        });

        if (savedInstanceState != null) {
            pager.setCurrentItem(savedInstanceState.getInt("page"));
            movieId = savedInstanceState.getString("movieId");
            userId = savedInstanceState.getString("userId");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        outState.putInt("page", pager.getCurrentItem());
        outState.putString("movieId", movieId);
        outState.putString("userId", userId);
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