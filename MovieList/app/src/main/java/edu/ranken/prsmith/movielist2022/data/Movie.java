package edu.ranken.prsmith.movielist2022.data;

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
}
