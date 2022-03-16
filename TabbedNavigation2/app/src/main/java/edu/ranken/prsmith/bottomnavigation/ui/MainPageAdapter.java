package edu.ranken.prsmith.bottomnavigation.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPageAdapter extends FragmentStateAdapter {

    public static final int POS_MOVIE_LIST = 0;
    public static final int POS_USER_LIST = 1;

    public MainPageAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case POS_MOVIE_LIST:
                return new MovieListFragment();
            case POS_USER_LIST:
                return new UserListFragment();
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}


