package ru.miacomsoft.exchangerates.LibWebViev;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import ru.miacomsoft.exchangerates.MainActivity;


public class Console {

    private long lastUpdate;
    private WebView webView;
    private MainActivity parentActivity;

    public Console(MainActivity activity, WebView webView) {
        this.webView = webView;
        parentActivity = activity;
        lastUpdate = System.currentTimeMillis();
    }

    /**
     * Вывод текста в консоли (отладка)
     *
     * @param msg
     */
    @JavascriptInterface
    public void log(String msg) {
        String[] arrOfStr = msg.split("\n");
        for (String line : arrOfStr) {
            Log.d("console.log", line);
        }
    }

    /**
     * Вывод ошибок в консоль  (отладка)
     *
     * @param msg
     */
    @JavascriptInterface
    public void error(String msg) {
        Log.d("console.log", "Error:" + msg);
    }

}
