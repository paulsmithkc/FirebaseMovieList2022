package edu.ranken.prsmith.movielist2022.ui;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MovieViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView director;
    public TextView genre;
    public ImageView image;
    public ImageButton upvote;
    public ImageButton downvote;

    public int voteValue;

    public MovieViewHolder(View itemView) {
        super(itemView);
    }
}
