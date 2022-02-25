package edu.ranken.prsmith.movielist2022.data;

import java.util.Map;

public class MovieSummary {
    public String id;
    public String name;
    public String director;
    public Integer releaseYear;
    public String image;
    public Map<String, Boolean> genre;

    public MovieSummary() {}

    public MovieSummary(Movie movie) {
        this.id = movie.id;
        this.name = movie.name;
        this.director = movie.director;
        this.releaseYear = movie.releaseYear;
        this.image = movie.image;
        this.genre = movie.genre;
    }
}
