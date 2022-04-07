package edu.ranken.prsmith.movielist2022.ui.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;

import java.util.LinkedHashMap;
import java.util.Map;

public class ErrorMessageContainer extends LiveData<Void> {
    private final Map<String, Integer> messages;

    public ErrorMessageContainer() {
        super();
        messages = new LinkedHashMap<>();
    }

    public void setMessage(@NonNull String key, @Nullable @StringRes Integer messageId) {
        messages.put(key, messageId);
        postValue(null);
    }

    public void showMessage(@NonNull Context context, @NonNull TextView view) {
        StringBuilder sb = new StringBuilder();

        for (Integer messageId : messages.values()) {
            if (messageId != null) {
                sb.append(context.getString(messageId)).append("\n");
            }
        }

        // remove trailing newline
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        view.setText(sb);
        view.setVisibility(sb.length() > 0 ? View.VISIBLE : View.GONE);
    }
}
