package edu.ranken.prsmith.movielist2022;

import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    // views
    private RecyclerView recyclerView;

    // state
    private FirebaseFirestore db;
    private MovieListAdapter adapter;
    private List<Movie> movies;
    private ListenerRegistration moviesRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        recyclerView = findViewById(R.id.movieList);

        // create adapter
        adapter = new MovieListAdapter(this, movies);

        // setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // firebase
        db = FirebaseFirestore.getInstance();

        // get realtime updates

        moviesRegistration =
            db.collection("movies")
                .orderBy("name")
                .limit(100)
                .addSnapshotListener(this, (QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e(LOG_TAG, "Error getting movies.", error);
                        Snackbar.make(recyclerView, error.getMessage(), Snackbar.LENGTH_SHORT).show();
                    } else {
                        List<Movie> newMovies =
                            querySnapshot != null ? querySnapshot.toObjects(Movie.class) : null;
                        movies = newMovies;
                        adapter.setItems(newMovies);

                        Snackbar.make(recyclerView, "Movies Updated.", Snackbar.LENGTH_SHORT).show();
                    }
                });

        // get data once

//        db.collection("movies")
//            .get()
//            .addOnCompleteListener(this, (Task<QuerySnapshot> task) -> {
//                if (!task.isSuccessful()) {
//                    Log.w(LOG_TAG, "Error getting movies.", task.getException());
//                } else {
//                    QuerySnapshot querySnapshot = task.getResult();
//
//                    // easy way
//                    List<Movie> newMovies =
//                        querySnapshot != null ? querySnapshot.toObjects(Movie.class) : null;
//                    movies = newMovies;
//                    adapter.setItems(newMovies);
//
//                    // hard way
//
////                    ArrayList<Movie> newMovies = new ArrayList<>();
////                    for (QueryDocumentSnapshot document : querySnapshot) {
////                        Log.d(LOG_TAG, document.getId() + " => " + document.getData());
////
////                        String movieId = document.getId();
////                        String movieName = (String) document.get("name");
////                        String movieDirector = (String) document.get("director");
////                        String movieImage = (String) document.get("image");
////                        Long movieYear = (Long) document.get("releaseYear");
////
////                        Movie movie = new Movie();
////                        movie.id = movieId;
////                        movie.name = movieName;
////                        movie.director = movieDirector;
////                        movie.image = movieImage;
////                        movie.releaseYear = movieYear != null ? movieYear.intValue() : null;
////                        newMovies.add(movie);
////                    }
////                    movies = newMovies;
////                    adapter.setItems(newMovies);
//                }
//            });
    }

    @Override
    protected void onDestroy() {
        if (moviesRegistration != null) {
            moviesRegistration.remove();
        }
        super.onDestroy();
    }
}