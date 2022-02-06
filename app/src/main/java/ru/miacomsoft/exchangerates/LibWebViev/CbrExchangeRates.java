package ru.miacomsoft.exchangerates.LibWebViev;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.miacomsoft.exchangerates.Common.CbrDaily;

public class CbrExchangeRates {

    private Context context;
    private WebView webView;
    private String CallbackJson = "";
    private JSONObject JsonCbr;

    public CbrExchangeRates(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
        this.webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:  CbrExchangeRates['jsonObj'] = {}");
            }
        });
    }

    /**
     * JS регистрируем имя JS функции , которая будет вызыватся после получения данных  курса валют
     *
     * @param CallbackJson - имя JS функции
     */
    @JavascriptInterface
    public void regCallback(String CallbackJson) {
        this.CallbackJson = CallbackJson;
    }

    /**
     * JS функция получения курса валют
     * Функция получения курса валют и запуска JS колбэк функции, с аргументом JSON объекта
     */
    @JavascriptInterface
    public void getJson() {
        try {
            CbrDaily cbrDaily = new CbrDaily(context, 1);
            cbrDaily.loadDailyJson(false, (JSONObject cbr) -> {
                if (CallbackJson.length() > 0) {
                    JsonCbr = cbr;
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl("javascript:  if(typeof(" + CallbackJson + ") === 'function'){ " + CallbackJson + "(" + JsonCbr.toString() + ");  }");
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Функция перезагрузки страницы
     */
    @JavascriptInterface
    public void reloadPage() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.reload();
            }
        });
    }
}
