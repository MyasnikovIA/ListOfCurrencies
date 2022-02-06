package ru.miacomsoft.exchangerates.Lib;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ru.miacomsoft.exchangerates.Interface.CallbackByteArr;
import ru.miacomsoft.exchangerates.Interface.CallbackString;
import ru.miacomsoft.exchangerates.Interface.CallbackJsonArray;
import ru.miacomsoft.exchangerates.Interface.CallbackJsonObject;

/**
 * Пример использования :
 * <p>
 * Получить текст по URL страке;
 * new HttpRequest().get("https://www.cbr-xml-daily.ru/daily_json.js",(String response) -> {
 * Log.d("MainActivity",response);
 * });
 * <p>
 * Получить Json объект по URL страке;
 * new HttpRequest().get("https://www.cbr-xml-daily.ru/daily_json.js",(JSONObject response) -> {
 * Log.d("MainActivity",response.toString());
 * });
 * <p>
 * Получить Json массив по URL страке;
 * new HttpRequest().get("https://www.cbr-xml-daily.ru/daily_json.js",(JSONObject response) -> {
 * Log.d("MainActivity",response.toString());
 * });
 * <p>
 * Получить битовый массив по URL страке;
 * new HttpRequest().get("https://www.cbr-xml-daily.ru/daily_json.js",(byte[] response) -> {
 * Log.d("MainActivity",new String(response) );
 * });
 */
public class HttpRequest {

    public CallbackJsonObject callbackJsonObject = null;

    public CallbackJsonArray callbackHttpRequestJsonArray = null;

    public CallbackString callbackHttpRequest = null;

    public CallbackByteArr callbackHttpByteArr = null;

    private Thread mainThready = null;

    private String urlQuery = "";

    public HttpRequest() {
    }

    /***
     * Получение JSON объекта из URL источника (https/http)
     * @param urlQuery - адрес запроса
     * @param callbackHttpRequest - функция обработки ответа (входящий аргумент строка)
     */
    public void get(String urlQuery, CallbackString callbackHttpRequest) {
        this.callbackHttpRequest = callbackHttpRequest;
        mainThready = new Thread(new Runnable() {
            public void run() {
                try {
                    callbackHttpRequest.call(new String(getJSON(urlQuery)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mainThready.start();
    }

    /***
     * Получение JSON объекта из URL источника (https/http)
     * @param urlQuery - адрес запроса
     * @param callbackJsonObject - функция обработки ответа  (входящий аргумент Json объект)
     */
    public void getJsonObject(String urlQuery, CallbackJsonObject callbackJsonObject) {
        this.callbackJsonObject = callbackJsonObject;
        mainThready = new Thread(new Runnable() {
            public void run() {
                try {
                    String jsonStr = new String(getJSON(urlQuery));
                    if (jsonStr.trim().substring(0, 1).equals("{")) {
                        callbackJsonObject.call(new JSONObject(jsonStr));
                    } else if (jsonStr.trim().substring(0, 1).equals("[")) {
                        JSONObject resultObj = new JSONObject();
                        resultObj.put("array", new JSONArray(jsonStr));
                        callbackJsonObject.call(resultObj);
                    } else {
                        JSONObject resultObj = new JSONObject();
                        resultObj.put("request", jsonStr);
                        callbackJsonObject.call(resultObj);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mainThready.start();
    }

    /***
     * Получение JSON объекта из URL источника (https/http)
     * @param urlQuery - адрес запроса
     * @param callbackHttpRequestJsonArray - функция обработки ответа  (входящий аргумент Json массив)
     */
    public void getJsonArray(String urlQuery, CallbackJsonArray callbackHttpRequestJsonArray) {
        this.callbackHttpRequestJsonArray = callbackHttpRequestJsonArray;
        mainThready = new Thread(new Runnable() {
            public void run() {
                try {
                    String jsonStr = new String(getJSON(urlQuery));
                    if (jsonStr.trim().substring(0, 1).equals("[")) {
                        callbackHttpRequestJsonArray.call(new JSONArray(jsonStr));
                    } else {
                        JSONArray resultArr = new JSONArray();
                        resultArr.put(jsonStr);
                        callbackHttpRequestJsonArray.call(resultArr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mainThready.start();
    }

    /**
     * Получить битовый массив из URL источника
     *
     * @param urlQuery
     * @param callbackHttpByteArr
     */
    public void getByteArray(String urlQuery, CallbackByteArr callbackHttpByteArr) {
        this.callbackHttpByteArr = callbackHttpByteArr;
        mainThready = new Thread(new Runnable() {
            public void run() {
                callbackHttpByteArr.call(getJSON(urlQuery));
            }
        });
        mainThready.start();
    }


    /**
     * Функция получения битового массива из URL источника (http/https)
     *
     * @param https_url
     * @return
     */
    private byte[] getJSON(String https_url) {
        try {
            URLConnection urlConnection;
            HttpURLConnection httpConn;
            URL url = new URL(https_url);
            if (url.getProtocol().toLowerCase().equals("https")) {
                trustAllHosts();
                httpConn = (HttpsURLConnection) url.openConnection();
            } else {
                httpConn = (HttpURLConnection) url.openConnection();
            }
            httpConn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            httpConn.connect();
            return getResponseMessage(httpConn).toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "".getBytes();
    }

    /**
     *  Получение витового контента из HTTP коннекта
     * @param connection
     * @return
     * @throws Exception
     */
    private ByteArrayOutputStream getResponseMessage(HttpURLConnection connection) throws Exception {
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream bufferReader = new ByteArrayOutputStream();
        int ch_tmp;
        while ((ch_tmp = inputStream.read()) != -1) {
            bufferReader.write(ch_tmp);
        }
        inputStream.close();
        return bufferReader;
    }


    /**
     * функция для использования HTTPS запросов
     */
    private void trustAllHosts() {
        X509TrustManager easyTrustManager = new X509TrustManager() {
            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };
        TrustManager[] trustAllCerts = new TrustManager[]{easyTrustManager};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
