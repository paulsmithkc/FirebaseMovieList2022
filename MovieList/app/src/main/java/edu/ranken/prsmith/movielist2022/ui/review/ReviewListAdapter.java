package edu.ranken.prsmith.movielist2022.ui.review;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import edu.ranken.prsmith.movielist2022.R;
import edu.ranken.prsmith.movielist2022.data.Review;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewViewHolder> {

    private static final String LOG_TAG = ReviewListAdapter.class.getSimpleName();

    private final Activity context;
    private final LayoutInflater layoutInflater;
    private List<Review> reviews;

    public ReviewListAdapter(Activity context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (reviews != null) {
            return reviews.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.review_item, parent, false);

        ReviewViewHolder vh = new ReviewViewHolder(itemView);
        vh.username = itemView.findViewById(R.id.item_review_username);
        vh.published = itemView.findViewById(R.id.item_review_published);
        vh.reviewText = itemView.findViewById(R.id.item_review_text);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder vh, int position) {
        Review review = reviews.get(position);

        vh.username.setText(review.username);
        vh.published.setText(formatDateTime(review.publishedOn));
        vh.reviewText.setText(review.reviewText);
    }

    private String formatDateTime(Date timestamp) {
        try {
            if (timestamp != null) {
                DateFormat outputFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
                return outputFormat.format(timestamp);
            } else {
                return context.getString(R.string.invalidDate);
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Failed to format: " + timestamp, ex);
            return context.getString(R.string.invalidDate);
        }
    }
}
