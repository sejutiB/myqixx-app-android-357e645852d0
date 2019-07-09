package qix.app.qix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.adapters.SettingsAdapter;

public class TutorialsActivity extends AppCompatActivity {

    private String[] list = {"Shake tutorial", "QIX Introduction", "QIX Travel video tutorial", "QIX Shake video tutorial", "QIX Marketplace video tutorial"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Settings");

        ListView mListView = (ListView) findViewById(R.id.ListView3);
        SettingsAdapter aAdapter = new SettingsAdapter(this, list);

        final ArrayList<String> List = new ArrayList<String>();
        for (int i = 0; i < list.length; ++i) {
            List.add(list[i]);
            mListView.setAdapter(aAdapter);
            mListView.setOnItemClickListener((parent, view, position, id) -> {
                switch (position) {
                    case 0:
                        Helpers.startNewActivity(ShakeTutorial.class);
                        break;
                    case 1:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=zGr8K5uNSfI&feature=youtu.be")));
                        break;
                    case 2:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=wdMJ6I7Er1g&feature=youtu.be")));
                        break;
                    case 3:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=QNdGohZUWZs&feature=youtu.be")));
                        break;
                    case 4:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=NB5aOT4iw9M&feature=youtu.be")));
                        break;
                }

            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
