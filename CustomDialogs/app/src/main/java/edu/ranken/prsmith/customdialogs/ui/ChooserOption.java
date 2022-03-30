package edu.ranken.prsmith.customdialogs.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class ChooserOption {
    private final Drawable icon;
    private final String name;
    private final Object value;

    public ChooserOption(@NonNull Context context, @DrawableRes int iconId, @NonNull String name, @Nullable Object value) {
        this(ContextCompat.getDrawable(context, iconId), name, value);
    }

    public ChooserOption(@Nullable Drawable icon, @NonNull String name, @Nullable Object value) {
        this.icon = icon;
        this.name = name;
        this.value = value;
    }

    @Nullable
    public Drawable getIcon() { return icon; }

    @NonNull
    public String getName() { return name; }

    @Nullable
    public Object getValue() { return value; }

    @Override
    public String toString() {
        return name;
    }
}
