package edu.ranken.prsmith.movielist2022.data;

import androidx.annotation.NonNull;

public enum MovieList {
    ALL_MOVIES("All Movies"),
    MY_VOTES("My Votes"),
    MY_UPVOTES("My Upvotes"),
    MY_DOWNVOTES("My Downvotes");

    private final String name;

    private MovieList(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
