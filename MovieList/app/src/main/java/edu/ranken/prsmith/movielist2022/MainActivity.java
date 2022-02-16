package edu.ranken.prsmith.movielist2022;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.ranken.prsmith.movielist2022.data.Movie;
import edu.ranken.prsmith.movielist2022.ui.MovieListAdapter;
import edu.ranken.prsmith.movielist2022.ui.MovieListViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    // views
    private RecyclerView recyclerView;

    // state
    private MovieListViewModel model;
    private MovieListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        recyclerView = findViewById(R.id.movieList);

        // create adapter
        adapter = new MovieListAdapter(this, null);

        // setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // bind model
        model = new ViewModelProvider(this).get(MovieListViewModel.class);
        model.getMovies().observe(this, (movies) -> {
            adapter.setItems(movies);
        });
        model.getErrorMessage().observe(this, (errorMessage) -> {
            if (errorMessage != null) Log.e(LOG_TAG, errorMessage);
            // errorText.setText(errorMessage);
        });
        model.getSnackbarMessage().observe(this, (snackbarMessage) -> {
            Snackbar.make(recyclerView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
            model.clearSnackbar();
        });
    }
}