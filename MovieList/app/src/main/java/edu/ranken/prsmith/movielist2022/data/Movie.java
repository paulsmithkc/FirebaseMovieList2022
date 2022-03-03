package edu.ranken.prsmith.movielist2022.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

import java.util.Map;

public class Movie {
    @DocumentId
    public String id;
    public String name;
    public String director;
    public Integer releaseYear;
    public String image;
    public Map<String, Boolean> genre;
    public String longDescription;

    public Movie() {}

    @NonNull
    @Override
    public String toString() {
        return "Movie {" + id + ", " + name + "}";
    }
}
