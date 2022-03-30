package edu.ranken.prsmith.customdialogs.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ConfirmDialog {
    private final AlertDialog dialog;

    public ConfirmDialog(
        @NonNull Context context,
        @NonNull CharSequence message,
        @NonNull OnClickListener onOk,
        @Nullable OnClickListener onCancel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            onOk.onClick(which);
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            if (onCancel != null) { onCancel.onClick(which); }
        });
        builder.setOnCancelListener((dialog) -> {
            if (onCancel != null) { onCancel.onClick(Dialog.BUTTON_NEGATIVE); }
        });
        dialog = builder.create();
    }

    public void show() { dialog.show(); }
    public void cancel() { dialog.cancel(); }

    public interface OnClickListener {
        void onClick(int which);
    }
}

