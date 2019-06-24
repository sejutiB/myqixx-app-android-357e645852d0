package qix.app.qix;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.helpers.Helpers;

public class WebviewActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        String url = getIntent().getStringExtra("paymentUrl");

        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            this.presentChoice();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.presentChoice();
    }

    private void presentChoice(){
        Helpers.presentSimpleDialog(this,"", getString(R.string.qix_exit_sure_message), getString(R.string.choice_yes), getString(R.string.choice_no), true, (dialogInterface, i) -> {

            this.closeWebView();
            dialogInterface.cancel();

        }, (dialogInterface, i) -> dialogInterface.dismiss());
    }

    private void closeWebView(){
        setResult(2);
        finish();
    }
}
