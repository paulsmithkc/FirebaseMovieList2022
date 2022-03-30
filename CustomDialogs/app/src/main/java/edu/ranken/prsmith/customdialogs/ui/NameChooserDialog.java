package edu.ranken.prsmith.customdialogs.ui;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NameChooserDialog {
    private final AlertDialog dialog;
    private final CharSequence[] names;

    public NameChooserDialog(
        @NonNull Context context,
        @NonNull CharSequence title,
        @NonNull CharSequence[] names,
        @NonNull OnChooseListener onChoose) {

        this.names = names;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(names, (dialog, which) -> {
            onChoose.onChoose(which, names[which]);
        });
        dialog = builder.create();
    }

    public void show() { dialog.show(); }
    public void cancel() { dialog.cancel(); }

    public interface OnChooseListener {
        void onChoose(int index, CharSequence name);
    }
}
