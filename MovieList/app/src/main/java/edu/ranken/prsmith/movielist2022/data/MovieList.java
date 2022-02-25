package edu.ranken.prsmith.movielist2022.data;

public enum MovieList {
    ALL_MOVIES("All Movies"),
    MY_VOTES("My Votes"),
    MY_UPVOTES("My Upvotes"),
    MY_DOWNVOTES("My Downvotes");

    private final String name;

    private MovieList(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
