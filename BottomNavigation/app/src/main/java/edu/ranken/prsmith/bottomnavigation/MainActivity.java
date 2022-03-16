package edu.ranken.prsmith.bottomnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import edu.ranken.prsmith.bottomnavigation.ui.MainPageAdapter;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 pager;
    private BottomNavigationView bottomNav;
    private MainPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        pager = findViewById(R.id.pager);
        bottomNav = findViewById(R.id.bottomNav);

        // create adapter
        adapter = new MainPageAdapter(this);
        pager.setAdapter(adapter);

        // register listeners
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNav.getMenu().getItem(position).setChecked(true);
            }
        });
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (item.getItemId() == R.id.actionMoviesList) {
                    pager.setCurrentItem(MainPageAdapter.POS_MOVIE_LIST);
                    return true;
                } else if (itemId == R.id.actionUsersList) {
                    pager.setCurrentItem(MainPageAdapter.POS_USER_LIST);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}