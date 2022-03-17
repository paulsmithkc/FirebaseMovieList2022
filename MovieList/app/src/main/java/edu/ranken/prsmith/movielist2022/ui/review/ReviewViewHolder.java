package edu.ranken.prsmith.movielist2022.ui.review;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    public TextView username;
    public TextView published;
    public TextView reviewText;

    public ReviewViewHolder(View itemView) {
        super(itemView);
    }
}
