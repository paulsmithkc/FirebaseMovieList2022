package edu.ranken.prsmith.customdialogs.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.ranken.prsmith.customdialogs.R;

public class ConsoleChooserDialog {
    private final AlertDialog dialog;
    private final Map<String, Boolean> supportedConsoles;
    private final Map<String, Boolean> selectedConsoles;

    public ConsoleChooserDialog(
        @NonNull Context context,
        @NonNull CharSequence title,
        @Nullable Map<String, Boolean> supportedConsoles,
        @Nullable Map<String, Boolean> selectedConsoles,
        @NonNull OnChooseListener onChoose
    ) {
        // get inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the layout
        View contentView = inflater.inflate(R.layout.console_chooser_dialog, null, false);

        // find views
        ImageButton xboxButton = contentView.findViewById(R.id.xboxButton);
        ImageButton playstationButton = contentView.findViewById(R.id.playstationButton);
        // ... other consoles

        // save maps to instance variables
        this.supportedConsoles = supportedConsoles;
        this.selectedConsoles = selectedConsoles != null ? selectedConsoles : new HashMap<>();

        // loop over keys and buttons
        String[] keys = { "xbox", "playstation" };
        ImageButton[] buttons = { xboxButton, playstationButton };
        for (int i = 0; i < keys.length; ++i) {
            String key = keys[i];
            boolean enabled = supportedConsoles == null || Objects.equals(supportedConsoles.get(key), Boolean.TRUE);
            boolean checkedInitially = Objects.equals(this.selectedConsoles.get(key), Boolean.TRUE);

            ImageButton button = buttons[i];
            button.setEnabled(enabled);
            // button.setBackgroundTintList();
            // button.setImageResource();
            button.setOnClickListener((view) -> {
                boolean checked = !Objects.equals(this.selectedConsoles.get(key), Boolean.TRUE);
                this.selectedConsoles.put(key, checked);
                // button.setBackgroundTintList();
                // button.setImageResource();
            });
        }

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(contentView);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            onChoose.onChoose(this.selectedConsoles);
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            // do nothing
        });
        dialog = builder.create();
    }

    public void show() { dialog.show(); }
    public void cancel() { dialog.cancel(); }

    public interface OnChooseListener {
        void onChoose(@NonNull Map<String, Boolean> selectedConsoles);
    }
}
