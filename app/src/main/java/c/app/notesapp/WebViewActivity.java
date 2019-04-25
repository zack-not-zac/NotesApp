package c.app.notesapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import java.util.Objects;

public class WebViewActivity extends Activity {

    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        Intent intent = getIntent();
        String URL = Objects.requireNonNull(intent.getExtras()).getString("URL");

        webView.loadUrl(URL);
    }
}