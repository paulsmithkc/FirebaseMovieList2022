package edu.ranken.prsmith.movielist2022.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

public class Genre {
    @DocumentId
    public String id;
    public String name;
    public String icon;

    public Genre() {}

    @NonNull
    @Override
    public String toString() {
        return "Genre {" + id + ", " + name + "}";
    }
}
