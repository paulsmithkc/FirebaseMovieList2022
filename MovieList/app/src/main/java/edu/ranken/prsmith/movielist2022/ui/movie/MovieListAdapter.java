package edu.ranken.prsmith.movielist2022.ui.movie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.ranken.prsmith.movielist2022.MovieDetailsActivity;
import edu.ranken.prsmith.movielist2022.R;
import edu.ranken.prsmith.movielist2022.data.MovieSummary;
import edu.ranken.prsmith.movielist2022.data.MovieVoteValue;

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    private final FragmentActivity context;
    private final LayoutInflater layoutInflater;
    private final Picasso picasso;
    private final MovieListViewModel model;
    private List<MovieSummary> movies;
    private List<MovieVoteValue> votes;

    public MovieListAdapter(FragmentActivity context, MovieListViewModel model) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.picasso = Picasso.get();
        this.model = model;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMovies(List<MovieSummary> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setVotes(List<MovieVoteValue> votes) {
        this.votes = votes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_movie, parent, false);

        MovieViewHolder vh = new MovieViewHolder(itemView);
        vh.name = itemView.findViewById(R.id.item_movie_name);
        vh.director = itemView.findViewById(R.id.item_movie_director);
        vh.image = itemView.findViewById(R.id.item_movie_image);
        vh.upvote = itemView.findViewById(R.id.item_movie_upvote);
        vh.downvote = itemView.findViewById(R.id.item_movie_downvote);

//        vh.genreIcons = new ImageView[3];
//        vh.genreIcons[0] = itemView.findViewById(R.id.item_movie_genre_1);
//        vh.genreIcons[1] = itemView.findViewById(R.id.item_movie_genre_2);
//        vh.genreIcons[2] = itemView.findViewById(R.id.item_movie_genre_3);

        vh.genreIcons = new ImageView[] {
            itemView.findViewById(R.id.item_movie_genre_1),
            itemView.findViewById(R.id.item_movie_genre_2),
            itemView.findViewById(R.id.item_movie_genre_3)
        };

        vh.upvote.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "upvote");
            MovieSummary movie = movies.get(vh.getAdapterPosition());
            if (vh.voteValue > 0) {
                model.removeVoteFromMovie(movie.id);
            } else {
                model.addUpvoteForMovie(movie);
            }
        });
        vh.downvote.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "downvote");
            MovieSummary movie = movies.get(vh.getAdapterPosition());
            if (vh.voteValue < 0) {
                model.removeVoteFromMovie(movie.id);
            } else {
                model.addDownvoteForMovie(movie);
            }
        });
        vh.itemView.setOnClickListener((view) -> {
            MovieSummary movie = movies.get(vh.getAdapterPosition());
            Log.i(LOG_TAG, "Clicked on movie: " + movie.id);

            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE_ID, movie.id);
            context.startActivity(intent);
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder vh, int position) {
        MovieSummary movie = movies.get(position);

        if (movie.name == null || movie.name.length() == 0) {
            vh.name.setText(R.string.nameMissing);
        } else if (movie.releaseYear == null) {
            vh.name.setText(movie.name);
        } else {
            // vh.name.setText(movie.name + " (" + movie.releaseYear + ")");
            vh.name.setText(context.getString(R.string.movieNameFormat, movie.name, movie.releaseYear));
        }

        if (movie.director == null || movie.director.length() == 0) {
            vh.director.setText(null);
            vh.director.setVisibility(View.GONE);
        } else {
            vh.director.setText(movie.director);
            vh.director.setVisibility(View.VISIBLE);
        }

        if (movie.image == null || movie.image.length() == 0) {
            vh.image.setImageResource(R.drawable.ic_broken_image);
        } else {
            vh.image.setImageResource(R.drawable.ic_downloading);
            this.picasso
                .load(movie.image)
                .noPlaceholder()
                //.placeholder(R.drawable.ic_downloading)
                .error(R.drawable.ic_error)
                .resizeDimen(R.dimen.movieThumbnailResizeWidth, R.dimen.movieThumbnailResizeHeight)
                .centerInside()
                .into(vh.image);
        }

        if (movie.genre == null) {
            for (int i = 0; i < vh.genreIcons.length; ++i) {
                vh.genreIcons[i].setImageResource(0);
                vh.genreIcons[i].setVisibility(View.GONE);
            }
        } else {
            int iconIndex = 0;
            for (Map.Entry<String, Boolean> entry : movie.genre.entrySet()) {
                if (Objects.equals(entry.getValue(), Boolean.TRUE)) {
                    switch (entry.getKey()) {
                        default:
                            vh.genreIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.genreIcons[iconIndex].setImageResource(R.drawable.ic_error);
                            vh.genreIcons[iconIndex].setContentDescription(context.getString(R.string.unknownGenre));
                            break;
                        case "action":
                            vh.genreIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.genreIcons[iconIndex].setImageResource(R.drawable.ic_action_20dp);
                            vh.genreIcons[iconIndex].setContentDescription(context.getString(R.string.actionGenre));
                            break;
                        case "comedy":
                            vh.genreIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.genreIcons[iconIndex].setImageResource(R.drawable.ic_comedy_20dp);
                            vh.genreIcons[iconIndex].setContentDescription(context.getString(R.string.comedyGenre));
                            break;
                        case "romance":
                            vh.genreIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.genreIcons[iconIndex].setImageResource(R.drawable.ic_romance_20dp);
                            vh.genreIcons[iconIndex].setContentDescription(context.getString(R.string.romanceGenre));
                            break;
                    }
                    iconIndex++;
                    if (iconIndex >= vh.genreIcons.length) {
                        break;
                    }
                }
            }
            for (; iconIndex < vh.genreIcons.length; ++iconIndex) {
                vh.genreIcons[iconIndex].setImageResource(0);
                vh.genreIcons[iconIndex].setVisibility(View.GONE);
            }
        }

        vh.upvote.setVisibility(votes == null ? View.GONE : View.VISIBLE);
        vh.downvote.setVisibility(votes == null ? View.GONE : View.VISIBLE);
        vh.upvote.setImageResource(R.drawable.ic_thumbs_up_outline);
        vh.downvote.setImageResource(R.drawable.ic_thumbs_down_outline);
        vh.voteValue = 0;

        if (votes != null) {
            for (MovieVoteValue vote : votes) {
                if (Objects.equals(movie.id, vote.movieId)) {
                    vh.voteValue = vote.value;
                    if (vote.value > 0) {
                        vh.upvote.setImageResource(R.drawable.ic_thumbs_up_solid);
                    } else if (vote.value < 0) {
                        vh.downvote.setImageResource(R.drawable.ic_thumbs_down_solid);
                    }
                    break;
                }
            }
        }
    }
}
