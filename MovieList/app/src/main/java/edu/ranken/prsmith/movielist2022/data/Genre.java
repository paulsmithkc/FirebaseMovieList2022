package edu.ranken.prsmith.movielist2022.data;

import com.google.firebase.firestore.DocumentId;

public class Genre {
    @DocumentId
    public String id;
    public String name;
    public String icon;

    public Genre() {}

    @Override
    public String toString() {
        return "Genre {" + id + ", " + name + "}";
    }
}
