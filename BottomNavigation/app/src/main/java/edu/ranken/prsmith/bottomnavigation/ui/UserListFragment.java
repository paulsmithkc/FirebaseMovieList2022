package edu.ranken.prsmith.bottomnavigation.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.ranken.prsmith.bottomnavigation.R;

public class UserListFragment extends Fragment {
    public UserListFragment() {
        super(R.layout.users_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // populate recycler view ...
    }
}

