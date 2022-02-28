package edu.ranken.prsmith.movielist2022.data;

import androidx.annotation.NonNull;

public class MovieVoteValue {
    public String movieId;
    public int value;

    public MovieVoteValue() {}

    public MovieVoteValue(@NonNull String movieId, int value) {
        this.movieId = movieId;
        this.value = value;
    }

    public MovieVoteValue(@NonNull MovieVote vote) {
        this.movieId = vote.movieId;
        this.value = vote.value.intValue();
    }

    @NonNull
    @Override
    public String toString() {
        return "MovieVoteValue {id=" + movieId + ", value=" + value + "}";
    }
}
