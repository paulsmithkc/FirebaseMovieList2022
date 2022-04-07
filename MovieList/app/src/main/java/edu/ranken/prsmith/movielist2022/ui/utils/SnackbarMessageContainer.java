package edu.ranken.prsmith.movielist2022.ui.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;

import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.Queue;

public class SnackbarMessageContainer extends LiveData<Void> {
    private final Queue<Integer> messages;
    private boolean showing;

    public SnackbarMessageContainer() {
        super();
        messages = new LinkedList<>();
        showing = false;
    }

    public void addMessage(@NonNull @StringRes Integer messageId) {
        messages.add(messageId);
        postValue(null);
    }

    public void showMessage(@NonNull Context context, @NonNull View view) {
        if (!showing) {
            Integer messageId = messages.poll();
            if (messageId != null) {
                showing = true;
                Snackbar snackbar = Snackbar.make(view, messageId, Snackbar.LENGTH_SHORT);
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        Log.e("SnackbarMessageContainer", "snackbar dismissed");
                        showing = false;
                        postValue(null);
                    }
                });
                snackbar.show();
            }
        }
    }
}
