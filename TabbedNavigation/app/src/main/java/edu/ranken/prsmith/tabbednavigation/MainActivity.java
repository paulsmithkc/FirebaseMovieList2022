package edu.ranken.prsmith.tabbednavigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import edu.ranken.prsmith.tabbednavigation.ui.ExamplePageAdapter;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 pager;
    private ExamplePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        tabLayout = findViewById(R.id.tabLayout);
        pager = findViewById(R.id.pager);

        // create adapter
        adapter = new ExamplePageAdapter(this);
        pager.setAdapter(adapter);

        // create mediator
        TabLayoutMediator mediator = new TabLayoutMediator(
            tabLayout, pager,
            (tab, position) -> tab.setText("TAB " + position)
        );
        mediator.attach();
    }
}