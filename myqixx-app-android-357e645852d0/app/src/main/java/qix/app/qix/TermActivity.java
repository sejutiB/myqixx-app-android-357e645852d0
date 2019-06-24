package qix.app.qix;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TermActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  getSupportActionBar().setHomeButtonEnabled(true);

        setContentView(R.layout.activity_term);

        WebView main = findViewById(R.id.termWebView);

        boolean terms = getIntent().getBooleanExtra("isTerms", true);


            if (terms) {
                main.loadUrl("file:///android_asset/terms.html");
                setTitle("Terms and Conditions");
            } else {
                main.loadUrl("file:///android_asset/privacy.html");
                setTitle("Privacy Policy");
            }
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }*/
}

