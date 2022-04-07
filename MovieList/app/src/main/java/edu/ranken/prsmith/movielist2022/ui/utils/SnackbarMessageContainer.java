package edu.ranken.prsmith.movielist2022.ui.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.Queue;

public class SnackbarMessageContainer {
    private final Queue<Integer> messages;

    public SnackbarMessageContainer() {
        messages = new LinkedList<>();
    }

    public void addMessage(@StringRes int messageId) {
        messages.add(messageId);
    }

    public void showMessage(@NonNull View view, @Nullable OnDismissListener onDismissListener) {
        Integer messageId = messages.poll();
        if (messageId != null) {
            Snackbar snackbar = Snackbar.make(view, messageId, Snackbar.LENGTH_SHORT);
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (onDismissListener != null) {
                        onDismissListener.onDismiss();
                    }
                }
            });
            snackbar.show();
        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
