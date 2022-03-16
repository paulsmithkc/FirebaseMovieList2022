package edu.ranken.prsmith.bottomnavigation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.NumberFormat;

import edu.ranken.prsmith.bottomnavigation.R;

public class ExamplePageFragment extends Fragment {
    public ExamplePageFragment() {
        super(R.layout.fragment_page);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // get args
        Bundle args = requireArguments();
        int position = args.getInt("position");

        // find views
        TextView textView = view.findViewById(R.id.textView);

        // populate views
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        textView.setText(numberFormat.format(position));
    }
}

