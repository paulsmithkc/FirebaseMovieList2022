package edu.ranken.prsmith.movielist2022.ui.movie;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import edu.ranken.prsmith.movielist2022.R;
import edu.ranken.prsmith.movielist2022.data.Genre;
import edu.ranken.prsmith.movielist2022.data.MovieList;
import edu.ranken.prsmith.movielist2022.ui.utils.SpinnerOption;

public class MovieListFragment extends Fragment {

    // constants
    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    // views
    private Spinner genreSpinner;
    private Spinner listSpinner;
    private TextView moviesError;
    private TextView votesError;
    private TextView genresError;
    private RecyclerView recyclerView;

    // state
    private MovieListViewModel model;
    private MovieListAdapter moviesAdapter;
    private ArrayAdapter<SpinnerOption<String>> genresAdapter;
    private ArrayAdapter<SpinnerOption<MovieList>> listAdapter;

    public MovieListFragment() {
        super(R.layout.movie_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views
        genreSpinner = view.findViewById(R.id.genreSpinner);
        listSpinner = view.findViewById(R.id.listSpinner);
        moviesError = view.findViewById(R.id.moviesError);
        votesError = view.findViewById(R.id.votesError);
        genresError = view.findViewById(R.id.genresError);
        recyclerView = view.findViewById(R.id.movieList);

        // get activity
        FragmentActivity activity = getActivity();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        // setup recycler view
        int columns = getResources().getInteger(R.integer.movieListColumns);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, columns));

        // setup view model and adapter
        model = new ViewModelProvider(this).get(MovieListViewModel.class);
        moviesAdapter = new MovieListAdapter(activity, model);
        recyclerView.setAdapter(moviesAdapter);

        // populate list spinner

        SpinnerOption<MovieList>[] listOptions;
        if (model.getUserId() != null) {
            listOptions = new SpinnerOption[]{
                new SpinnerOption<>(getString(R.string.allMovies), MovieList.ALL_MOVIES),
                new SpinnerOption<>(getString(R.string.myVotes), MovieList.MY_VOTES),
                new SpinnerOption<>(getString(R.string.myUpvotes), MovieList.MY_UPVOTES),
                new SpinnerOption<>(getString(R.string.myDownvotes), MovieList.MY_DOWNVOTES)
            };
        } else {
            listOptions = new SpinnerOption[]{
                new SpinnerOption<>(getString(R.string.allMovies), MovieList.ALL_MOVIES)
            };
        }

        listAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, listOptions);
        listSpinner.setAdapter(listAdapter);

        if (savedInstanceState != null) {
            int genreSelectedIndex = savedInstanceState.getInt("genreSelectedIndex");
            int listSelectedIndex = savedInstanceState.getInt("listSelectedIndex");
            genreSpinner.setSelection(genreSelectedIndex);
            listSpinner.setSelection(listSelectedIndex);
        }

        // observe model
        model.getMovies().observe(lifecycleOwner, (movies) -> {
            moviesAdapter.setMovies(movies);
        });
        model.getVotes().observe(lifecycleOwner, (votes) -> {
            moviesAdapter.setVotes(votes);
        });
        model.getGenres().observe(lifecycleOwner, (genres) -> {
            if (genres != null) {
                int selectedPosition = 0;
                String selectedId = model.getFilterGenreId();

                ArrayList<SpinnerOption<String>> genreNames = new ArrayList<>(genres.size());
                genreNames.add(new SpinnerOption<>(getString(R.string.allGenres), null));

                for (int i = 0; i < genres.size(); ++i) {
                    Genre genre = genres.get(i);
                    if (genre.id != null && genre.name != null) {
                        genreNames.add(new SpinnerOption<>(genre.name, genre.id));
                        if (Objects.equals(genre.id, selectedId)) {
                            selectedPosition = genreNames.size() - 1;
                        }
                    }
                }

                genresAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, genreNames);
                genreSpinner.setAdapter(genresAdapter);
                genreSpinner.setSelection(selectedPosition, false);
            }
        });
        model.getMoviesError().observe(lifecycleOwner, (messageId) -> {
            if (messageId != null) {
                moviesError.setText(messageId);
                moviesError.setVisibility(View.VISIBLE);
            } else {
                moviesError.setText(null);
                moviesError.setVisibility(View.GONE);
            }
        });
        model.getVotesError().observe(lifecycleOwner, (messageId) -> {
            if (messageId != null) {
                votesError.setText(messageId);
                votesError.setVisibility(View.VISIBLE);
            } else {
                votesError.setText(null);
                votesError.setVisibility(View.GONE);
            }
        });
        model.getGenresError().observe(lifecycleOwner, (messageId) -> {
            if (messageId != null) {
                genresError.setText(messageId);
                genresError.setVisibility(View.VISIBLE);
            } else {
                genresError.setText(null);
                genresError.setVisibility(View.GONE);
            }
        });
        model.getSnackbarMessage().observe(lifecycleOwner, (messageId) -> {
            if (messageId != null) {
                Snackbar.make(recyclerView, messageId, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            } else {
                // no message to show
            }
        });

        // register listeners
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption<String> option = (SpinnerOption<String>) parent.getItemAtPosition(position);
                model.filterMoviesByGenre(option.getValue());
                Log.i(LOG_TAG, "Filter by genre: " + option.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption<MovieList> option = (SpinnerOption<MovieList>) parent.getItemAtPosition(position);
                model.filterMoviesByList(option.getValue());
                Log.i(LOG_TAG, "Filter by list: " + option.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        outState.putInt("genreSelectedIndex", genreSpinner.getSelectedItemPosition());
        outState.putInt("listSelectedIndex", listSpinner.getSelectedItemPosition());
    }
}
