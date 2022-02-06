package ru.miacomsoft.exchangerates;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.miacomsoft.exchangerates.Common.CbrDaily;
import ru.miacomsoft.exchangerates.Interface.CallbackString;
import ru.miacomsoft.exchangerates.Lib.UserWebClient;
import ru.miacomsoft.exchangerates.LibWebViev.CbrExchangeRates;
import ru.miacomsoft.exchangerates.LibWebViev.Console;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        webView = (WebView) findViewById(R.id.webView);
        webView.requestFocus();
        webView.setSoundEffectsEnabled(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webView.addJavascriptInterface(new Console(this, webView), "console");
        webView.addJavascriptInterface(new CbrExchangeRates(this, webView), "CbrExchangeRates");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        UserWebClient userWebClient = new UserWebClient(progressBar);
        webView.setWebViewClient(userWebClient);
        try {
            new CbrDaily(this).loadDailyJson(false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        if (webView != null) {
            if (Build.VERSION.SDK_INT >= 11) {
                webView.onResume();
            }
            webView.resumeTimers();
        }
        super.onResume();
    }
}