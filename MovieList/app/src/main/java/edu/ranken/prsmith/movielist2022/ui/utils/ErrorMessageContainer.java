package edu.ranken.prsmith.movielist2022.ui.utils;

import android.content.Context;

import androidx.annotation.StringRes;

import java.util.LinkedHashMap;
import java.util.Map;

public class ErrorMessageContainer {
    private final Map<String, Integer> messages;

    public ErrorMessageContainer() {
        messages = new LinkedHashMap<>();
    }

    public void setMessage(String key, @StringRes int messageId) {
        messages.put(key, messageId);
    }

    public CharSequence getMessages(Context context) {
        StringBuilder sb = new StringBuilder();

        for (Integer messageId : messages.values()) {
            if (messageId != null) {
                sb.append(context.getString(messageId)).append("\n");
            }
        }

        return sb;
    }
}
