package edu.ranken.prsmith.customdialogs.data;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Thing {
    private final String name;
    private final String imageUrl;

    public Thing(@Nullable String name, @Nullable String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    @Nullable
    public String getName() { return name; }

    @Nullable
    public String getImageUrl() { return imageUrl; }

    @Override
    public String toString() {
        return name;
    }
}
