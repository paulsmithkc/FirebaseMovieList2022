package edu.ranken.prsmith.customdialogs.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdapterChooserDialog<T> {
    private final AlertDialog dialog;
    private final ArrayAdapter<T> adapter;

    public AdapterChooserDialog(
        @NonNull Context context,
        @NonNull CharSequence title,
        @NonNull ArrayAdapter<T> adapter,
        @NonNull OnChooseListener<T> onChoose) {

        this.adapter = adapter;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setSingleChoiceItems(adapter, -1, (dialog, which) -> {
            dialog.dismiss();
            onChoose.onChoose(which, adapter.getItem(which));
        });

        dialog = builder.create();
    }

    public void show() { dialog.show(); }
    public void cancel() { dialog.cancel(); }

    public interface OnChooseListener<T> {
        void onChoose(int index, T item);
    }
}
