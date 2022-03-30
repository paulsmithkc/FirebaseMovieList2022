package edu.ranken.prsmith.customdialogs.ui;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChooserOption {
    private final Drawable icon;
    private final CharSequence text;
    private final Object value;

    public ChooserOption(@Nullable Drawable icon, @NonNull CharSequence text, @Nullable Object value) {
        this.icon = icon;
        this.text = text;
        this.value = value;
    }

    @Nullable
    public Drawable getIcon() { return icon; }

    @NonNull
    public CharSequence getText() { return text; }

    @Nullable
    public Object getValue() { return value; }

    @Override
    public String toString() {
        return text.toString();
    }
}
