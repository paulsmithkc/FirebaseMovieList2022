package edu.ranken.prsmith.movielist2022.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Review {

    @DocumentId
    public String id;
    public String movieId;
    public String username;
    public String reviewText;
    @ServerTimestamp
    public Date publishedOn;

    public Review() {}

    @NonNull
    @Override
    public String toString() {
        return "Review {id=" + id + "}";
    }
}
