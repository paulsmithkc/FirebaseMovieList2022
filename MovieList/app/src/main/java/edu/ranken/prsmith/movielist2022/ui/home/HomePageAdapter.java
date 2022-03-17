package edu.ranken.prsmith.movielist2022.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import edu.ranken.prsmith.movielist2022.ui.movie.MovieListFragment;
import edu.ranken.prsmith.movielist2022.ui.user.UserListFragment;

public class HomePageAdapter extends FragmentStateAdapter {
    public HomePageAdapter(FragmentActivity activity) { super(activity); }
    public HomePageAdapter(Fragment fragment) { super(fragment); }

    @Override
    public int getItemCount() {
        return 2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new MovieListFragment();
            case 1: return new UserListFragment();
            default: throw new IndexOutOfBoundsException();
        }
    }
}
