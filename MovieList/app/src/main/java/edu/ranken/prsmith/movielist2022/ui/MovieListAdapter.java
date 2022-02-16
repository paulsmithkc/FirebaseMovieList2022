package edu.ranken.prsmith.movielist2022.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.ranken.prsmith.movielist2022.R;
import edu.ranken.prsmith.movielist2022.data.Movie;

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private AppCompatActivity context;
    private LayoutInflater layoutInflater;
    private List<Movie> items;

    public MovieListAdapter(AppCompatActivity context, List<Movie> items) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
    }

    public void setItems(List<Movie> items) {
        this.items = items;
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
        vh.genre = itemView.findViewById(R.id.item_movie_genre);

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
        }

        if (item.genre == null) {
            vh.genre.setText("");
        } else {
            StringBuilder genres = new StringBuilder();
            for (Map.Entry<String, Boolean> entry : item.genre.entrySet()) {
                if (Objects.equals(entry.getValue(), Boolean.TRUE)) {
                    genres.append(entry.getKey()).append(" ");
                }
            }
            vh.genre.setText(genres);
        }
    }
}
