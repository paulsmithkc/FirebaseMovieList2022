package edu.ranken.prsmith.movielist2022.data;

public class GenreFilter {
    public final String genreId;
    public final String genreName;

    public GenreFilter(String genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    @Override
    public String toString() {
        // FIXME: return a placeholder string, when the genre does not have a name
        return genreName;
    }
}
