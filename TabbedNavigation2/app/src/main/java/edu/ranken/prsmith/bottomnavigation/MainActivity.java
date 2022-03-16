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
import com.google.android.material.tabs.TabLayoutMediator;

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

        // create mediator
        String[] tabNames = {
            getString(R.string.movieListPage),
            getString(R.string.userListPage)
        };
        TabLayoutMediator mediator = new TabLayoutMediator(
            tabLayout, pager,
            (tab, position) -> tab.setText(tabNames[position])
        );
        mediator.attach();
    }
}