package edu.ranken.prsmith.movielist2022.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.ranken.prsmith.movielist2022.R;
import edu.ranken.prsmith.movielist2022.data.Movie;
import edu.ranken.prsmith.movielist2022.data.MovieVoteValue;

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private static final String LOG_TAG = "MovieListAdapter";

    private final AppCompatActivity context;
    private final LayoutInflater layoutInflater;
    private final Picasso picasso;
    private final MovieListViewModel model;
    private List<Movie> items;
    private List<MovieVoteValue> votes;

    public MovieListAdapter(AppCompatActivity context, MovieListViewModel model) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.picasso = Picasso.get();
        this.model = model;
    }

    public void setItems(List<Movie> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setVotes(List<MovieVoteValue> votes) {
        this.votes = votes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
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
        //vh.genre = itemView.findViewById(R.id.item_movie_genre);
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
            Movie movie = items.get(vh.getAdapterPosition());
            if (vh.voteValue > 0) {
                model.clearVote(movie.id);
            } else {
                model.upvote(movie.id);
            }
        });
        vh.downvote.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "downvote");
            Movie movie = items.get(vh.getAdapterPosition());
            if (vh.voteValue < 0) {
                model.clearVote(movie.id);
            } else {
                model.downvote(movie.id);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder vh, int position) {
        Movie item = items.get(position);

        if (item.name == null || item.name.length() == 0) {
            vh.name.setText(R.string.nameMissing);
        } else if (item.releaseYear == null) {
            vh.name.setText(item.name);
        } else {
            // vh.name.setText(item.name + " (" + item.releaseYear + ")");
            vh.name.setText(context.getString(R.string.movieNameFormat, item.name, item.releaseYear));
        }

        if (item.director == null || item.director.length() == 0) {
            vh.director.setText("");
        } else {
            vh.director.setText(item.director);
        }

        if (item.image == null || item.image.length() == 0) {
            vh.image.setImageResource(R.drawable.ic_broken_image);
        } else {
            vh.image.setImageResource(R.drawable.ic_downloading);
            this.picasso
                .load(item.image)
                .noPlaceholder()
                //.placeholder(R.drawable.ic_downloading)
                .error(R.drawable.ic_error)
                .resize(200, 300)
                .centerInside()
                .into(vh.image);
        }

        if (item.genre == null) {
            for (int i = 0; i < vh.genreIcons.length; ++i) {
                vh.genreIcons[i].setImageResource(0);
                vh.genreIcons[i].setVisibility(View.GONE);
            }
        } else {
            int iconIndex = 0;
            for (Map.Entry<String, Boolean> entry : item.genre.entrySet()) {
                if (Objects.equals(entry.getValue(), Boolean.TRUE)) {
                    vh.genreIcons[0].setVisibility(View.VISIBLE);
                    switch (entry.getKey()) {
                        default:
                            vh.genreIcons[iconIndex++].setImageResource(R.drawable.ic_error);
                            break;
                        case "action":
                            vh.genreIcons[iconIndex++].setImageResource(R.drawable.ic_action);
                            break;
                        case "comedy":
                            vh.genreIcons[iconIndex++].setImageResource(R.drawable.ic_comedy);
                            break;
                        case "romance":
                            vh.genreIcons[iconIndex++].setImageResource(R.drawable.ic_romance);
                            break;
                    }
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
                if (Objects.equals(item.id, vote.movieId)) {
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
