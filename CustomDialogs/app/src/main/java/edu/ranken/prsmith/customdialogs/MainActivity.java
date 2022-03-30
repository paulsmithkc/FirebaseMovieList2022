package edu.ranken.prsmith.customdialogs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import edu.ranken.prsmith.customdialogs.data.Thing;
import edu.ranken.prsmith.customdialogs.ui.AdapterChooserDialog;
import edu.ranken.prsmith.customdialogs.ui.ChooserOption;
import edu.ranken.prsmith.customdialogs.ui.ChooserOptionAdapter;
import edu.ranken.prsmith.customdialogs.ui.ConfirmDialog;
import edu.ranken.prsmith.customdialogs.ui.NameChooserDialog;
import edu.ranken.prsmith.customdialogs.ui.ThingChooserAdapter;
import edu.ranken.prsmith.customdialogs.ui.ThingChooserDialog;

public class MainActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // views
    private Button confirmButton;
    private Button chooserButton1;
    private Button chooserButton2;
    private Button chooserButton3;
    private Button chooserButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        confirmButton = findViewById(R.id.confirmButton);
        chooserButton1 = findViewById(R.id.chooserButton1);
        chooserButton2 = findViewById(R.id.chooserButton2);
        chooserButton3 = findViewById(R.id.chooserButton3);
        chooserButton4 = findViewById(R.id.chooserButton4);

        // register listeners
        confirmButton.setOnClickListener((view) -> showConfirmDialog());
        chooserButton1.setOnClickListener((view) -> showChooser1());
        chooserButton2.setOnClickListener((view) -> showChooser2());
        chooserButton3.setOnClickListener((view) -> showChooser3());
        chooserButton4.setOnClickListener((view) -> showChooser4());
    }

    private void showConfirmDialog() {
        Context context = this;
        ConfirmDialog dialog = new ConfirmDialog(
            context,
            "Are you sure that you want to do that?",
            (which) -> { Toast.makeText(context,"okay", Toast.LENGTH_SHORT).show(); },
            (which) -> { Toast.makeText(context,"cancel", Toast.LENGTH_SHORT).show(); }
        );
        dialog.show();
    }

    private void showChooser1() {
        Context context = this;
        String[] names = { "Fred", "Joe", "Moe" };

        NameChooserDialog dialog = new NameChooserDialog(
            context,
            "Name Chooser",
            names,
            (index, name) -> {
                Toast.makeText(context,"You chose " + name + ".", Toast.LENGTH_SHORT).show();
            }
        );
        dialog.show();
    }

    private void showChooser2() {
        Context context = this;

        ArrayList<Thing> items = new ArrayList<>();
        items.add(new Thing("Matrix", "https://www.themoviedb.org/t/p/w220_and_h330_face/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg"));
        items.add(new Thing("Matrix Reloaded", "https://www.themoviedb.org/t/p/w220_and_h330_face/9TGHDvWrqKBzwDxDodHYXEmOE6J.jpg"));
        items.add(new Thing("Matrix Revolutions", "https://www.themoviedb.org/t/p/w220_and_h330_face/qEWiBXJGXK28jGBAm8oFKKTB0WD.jpg"));

        ThingChooserDialog dialog = new ThingChooserDialog(
            context,
            "Thing Chooser",
            items,
            (index, item) -> {
                Toast.makeText(context,"You chose " + item.getName() + ".", Toast.LENGTH_SHORT).show();
            }
        );
        dialog.show();
    }

    private void showChooser3() {
        Context context = this;

        ArrayList<Thing> items = new ArrayList<>();
        items.add(new Thing("Matrix", "https://www.themoviedb.org/t/p/w220_and_h330_face/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg"));
        items.add(new Thing("Matrix Reloaded", "https://www.themoviedb.org/t/p/w220_and_h330_face/9TGHDvWrqKBzwDxDodHYXEmOE6J.jpg"));
        items.add(new Thing("Matrix Revolutions", "https://www.themoviedb.org/t/p/w220_and_h330_face/qEWiBXJGXK28jGBAm8oFKKTB0WD.jpg"));

        //ArrayAdapter<ChooserOption> adapter = new ArrayAdapter<>(context, R.layout.item_option, R.id.item_option_name, items);
        ThingChooserAdapter adapter = new ThingChooserAdapter(context, items);

        AdapterChooserDialog<Thing> dialog = new AdapterChooserDialog<>(
            context,
            "Adapter Chooser",
            adapter,
            (index, item) -> {
                Toast.makeText(context,"You chose " + item.getName() + ".", Toast.LENGTH_SHORT).show();
            }
        );
        dialog.show();
    }

    private void showChooser4() {
        Context context = this;

        ArrayList<ChooserOption> options = new ArrayList<>();
        options.add(new ChooserOption(ContextCompat.getDrawable(context, R.drawable.ic_action), "Action", "action"));
        options.add(new ChooserOption(ContextCompat.getDrawable(context, R.drawable.ic_comedy), "Comedy", "comedy"));
        options.add(new ChooserOption(ContextCompat.getDrawable(context, R.drawable.ic_romance), "Romance", "romance"));

        //ArrayAdapter<ChooserOption> adapter = new ArrayAdapter<>(context, R.layout.item_option, R.id.item_option_name, options);
        ChooserOptionAdapter adapter = new ChooserOptionAdapter(context, options);

        AdapterChooserDialog<ChooserOption> dialog = new AdapterChooserDialog<>(
            context,
            "Adapter Chooser",
            adapter,
            (index, item) -> {
                Toast.makeText(context,"You chose " + item.getValue() + ".", Toast.LENGTH_SHORT).show();
            }
        );
        dialog.show();
    }
}