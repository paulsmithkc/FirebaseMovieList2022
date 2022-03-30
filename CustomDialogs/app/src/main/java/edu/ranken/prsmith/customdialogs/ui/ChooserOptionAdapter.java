package edu.ranken.prsmith.customdialogs.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import edu.ranken.prsmith.customdialogs.R;

public class ChooserOptionAdapter extends ArrayAdapter<ChooserOption> {

    private final LayoutInflater inflater;

    public ChooserOptionAdapter(@NonNull Context context, @NonNull List<ChooserOption> options) {
        super(context, R.layout.item_option, R.id.item_option_name, options);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // inflate item layout
        View itemView;
        if (convertView != null) {
            itemView = convertView;
        } else {
            itemView = inflater.inflate(R.layout.item_option, parent, false);
        }

        // find child views
        ImageView iconView = itemView.findViewById(R.id.item_option_icon);
        TextView nameView = itemView.findViewById(R.id.item_option_name);

        // bind data from item
        ChooserOption option = getItem(position);
        iconView.setImageDrawable(option.getIcon());
        nameView.setText(option.getText());

        // return item view
        return itemView;
    }
}
