package edu.ranken.prsmith.customdialogs.ui;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import edu.ranken.prsmith.customdialogs.data.Thing;

public class ThingChooserDialog {
    private final AlertDialog dialog;
    private final List<Thing> items;
    private final CharSequence[] names;

    public ThingChooserDialog(
        @NonNull Context context,
        @NonNull CharSequence title,
        @NonNull List<Thing> items,
        @NonNull OnChooseListener onChoose) {

        // map the list of things to a list of names
        int n = items.size();
        this.items = items;
        this.names = new CharSequence[n];
        for (int i = 0; i < n; ++i) {
            names[i] = items.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(names, (dialog, which) -> {
            onChoose.onChoose(which, items.get(which));
        });
        dialog = builder.create();
    }

    public void show() { dialog.show(); }
    public void cancel() { dialog.cancel(); }

    public interface OnChooseListener {
        void onChoose(int index, Thing item);
    }
}
