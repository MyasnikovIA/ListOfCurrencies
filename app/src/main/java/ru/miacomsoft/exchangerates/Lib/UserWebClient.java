package ru.miacomsoft.exchangerates.Lib;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserWebClient extends WebViewClient {
    private ProgressBar progressBar = null;

    public UserWebClient(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}


