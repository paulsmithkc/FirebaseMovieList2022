package edu.ranken.prsmith.bottomnavigation.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.ranken.prsmith.bottomnavigation.R;

public class MovieListFragment extends Fragment {
    public MovieListFragment() {
        super(R.layout.movies_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // populate recycler view ...
    }
}

