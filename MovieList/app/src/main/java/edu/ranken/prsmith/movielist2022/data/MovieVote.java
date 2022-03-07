package edu.ranken.prsmith.movielist2022.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MovieVote {

    @DocumentId
    public String id;
    public String movieId;
    public String userId;
    public Long value;
    @ServerTimestamp
    public Date votedOn;

    public MovieSummary movie;

    public MovieVote() {
    }

    @NonNull
    @Override
    public String toString() {
        return "MovieVote {id=" + id + ", value=" + value + "}";
    }
}
