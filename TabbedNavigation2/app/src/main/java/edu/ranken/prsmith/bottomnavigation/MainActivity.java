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
    private TabLayout tabLayout;
    private ViewPager2 pager;
    private MainPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        tabLayout = findViewById(R.id.tabLayout);
        pager = findViewById(R.id.pager);

        // create adapter
        adapter = new MainPageAdapter(this);
        pager.setAdapter(adapter);

        // add tabs
        tabLayout.addTab(tabLayout.newTab().setText(R.string.movieListPage));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.userListPage));

        // register listeners
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                TabLayout.Tab tab = tabLayout.getTabAt(position);
                tabLayout.selectTab(tab);
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }
        });
    }
}