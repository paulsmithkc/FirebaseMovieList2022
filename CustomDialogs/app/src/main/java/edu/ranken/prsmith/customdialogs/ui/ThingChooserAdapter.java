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

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.ranken.prsmith.customdialogs.R;
import edu.ranken.prsmith.customdialogs.data.Thing;

public class ThingChooserAdapter extends ArrayAdapter<Thing> {

    private final LayoutInflater inflater;

    public ThingChooserAdapter(
        @NonNull Context context,
        @NonNull List<Thing> items) {

        super(context, R.layout.item_option, R.id.item_option_name, items);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(
        int position,
        @Nullable View convertView,
        @NonNull ViewGroup parent) {

        // inflate item layout
        View itemView;
        if (convertView != null) {
            itemView = convertView;
        } else {
            itemView = inflater.inflate(R.layout.item_option, parent, false);
        }

        // find child views
        TextView nameView = itemView.findViewById(R.id.item_option_name);
        ImageView iconView = itemView.findViewById(R.id.item_option_icon);

        // bind data from item

        Thing item = getItem(position);
        String itemName = item.getName();
        String itemImagUrl = item.getImageUrl();

        if (itemName == null) {
            nameView.setText(R.string.name_missing);
        } else {
            nameView.setText(itemName);
        }

        if (itemImagUrl == null) {
            iconView.setImageResource(R.drawable.ic_broken_image);
        } else {
            iconView.setImageResource(R.drawable.ic_downloading);
            Picasso
                .get()
                .load(itemImagUrl)
                .noPlaceholder()
                .error(R.drawable.ic_broken_image)
                .resize(64, 64)
                .centerInside()
                .into(iconView);
        }

        // return item view
        return itemView;
    }
}
