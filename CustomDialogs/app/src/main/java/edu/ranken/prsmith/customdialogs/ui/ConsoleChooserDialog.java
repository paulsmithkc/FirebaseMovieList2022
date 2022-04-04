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

        // supported
        if (supportedConsoles == null) {
            this.supportedConsoles = null;
        } else {
            this.supportedConsoles = supportedConsoles;

            boolean xbox = Objects.equals(supportedConsoles.get("xbox"), Boolean.TRUE);
            xboxButton.setEnabled(xbox);
            // ... other consoles
        }

        // selected
        if (selectedConsoles == null) {
            this.selectedConsoles = new HashMap<>();
        } else {
            this.selectedConsoles = selectedConsoles;

            boolean xbox = Objects.equals(selectedConsoles.get("xbox"), Boolean.TRUE);
            // xboxButton.setBackgroundTintList();
            // xboxButton.setImageResource();

            // ... other consoles
        }

        // register listeners
        xboxButton.setOnClickListener((view) -> {
            boolean checked = Objects.equals(this.selectedConsoles.get("xbox"), Boolean.TRUE);
            this.selectedConsoles.put("xbox", !checked);
            // xboxButton.setBackgroundTintList();
            // xboxButton.setImageResource();
        });
        // ... other consoles

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

    public interface OnChooseListener {
        void onChoose(@NonNull Map<String, Boolean> selectedConsoles);
    }
}
