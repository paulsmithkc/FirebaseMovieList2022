package edu.ranken.prsmith.bottomnavigation.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ExamplePageAdapter extends FragmentStateAdapter {

    public ExamplePageAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new ExamplePageFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);

        return fragment;
    }
}


