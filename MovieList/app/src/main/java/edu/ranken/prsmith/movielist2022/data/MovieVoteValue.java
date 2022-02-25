package edu.ranken.prsmith.movielist2022.data;

public class MovieVoteValue {
    public String movieId;
    public int value;

    public MovieVoteValue() {}

    public MovieVoteValue(String movieId, int value) {
        this.movieId = movieId;
        this.value = value;
    }

    public MovieVoteValue(MovieVote vote) {
        this.movieId = vote.movieId;
        this.value = vote.value.intValue();
    }

    @Override
    public String toString() {
        return "MovieVoteValue {id=" + movieId + ", value=" + value + "}";
    }
}
