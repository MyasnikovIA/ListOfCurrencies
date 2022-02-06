package ru.miacomsoft.exchangerates.Common;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import ru.miacomsoft.exchangerates.Interface.CallbackJsonObject;
import ru.miacomsoft.exchangerates.Lib.HttpRequest;

/**
 *  Класс предназначени для получение Json объекта курса валют из url адреса, с локальной буферизацией файла
 *
 *  Автоматическое обновление локального файла происходит 1 раз в 2 часа, если необходимо изменить это время,
 *  то при инициализации объекта вторым атрибутом можно задать количество часов, через сколько необходимо обновлять локальный файл.
 *  Файл обновится при потытке получить курс валют данные
 *
 *
 *  Применение:
 *       CbrDaily cbrDaily = new CbrDaily(this,1);
 *       cbrDaily.loadDailyJson(false, (JSONObject cbr) -> {
 *           if (!cbr.has("Valute")) return;
 *           Log.d("MainActivity", cbr.getJSONObject("Valute").getJSONObject("JPY").toString());
 *       });
 */
public class CbrDaily {

    private CallbackJsonObject callbackCbrDaily;
    private String urlDailyJson = "https://www.cbr-xml-daily.ru/daily_json.js";
    private String localFileName = "daily_json.json";
    private File fileJson;
    private Context context;
    private int hourUpdate = 2;

    private static final long HOUR = 3600 * 1000; // 1час

    /**
     * Конструктор объекта
     *
     * @param context - контекст приложения
     */
    public CbrDaily(Context context) {
        this.context = context;
    }

    /**
     * Конструктор объекта
     *
     * @param context
     * @param hourUpdate - через сколько часов снова скачать курс валют
     */
    public CbrDaily(Context context, int hourUpdate) {
        this.context = context;
        this.hourUpdate = hourUpdate;
    }

    /**
     * @param context      - контекст приложения
     * @param hourUpdate   - через сколько часов снова скачать курс валют
     * @param urlDailyJson - URL адрес источника курса валют
     */
    public CbrDaily(Context context, int hourUpdate, String urlDailyJson) {
        this.context = context;
        this.hourUpdate = hourUpdate;
        this.urlDailyJson = urlDailyJson;
    }

    /**
     * Конструктор объекта
     *
     * @param context      - контекст приложения
     * @param urlDailyJson - URL адрес источника курса валют
     */
    public CbrDaily(Context context, String urlDailyJson) {
        this.urlDailyJson = urlDailyJson;
        this.context = context;
    }

    /**
     * Конструктор объекта
     *
     * @param context        - контекст приложения
     * @param hourUpdate     - через сколько часов снова скачать курс валют
     * @param urlDailyJson-  URL адрес источника курса валют
     * @param localFileName- имя файла , куда будет сохранятся выгрузка из URL адреса (источника курса валют )
     */
    public CbrDaily(Context context, int hourUpdate, String urlDailyJson, String localFileName) {
        this.context = context;
        this.hourUpdate = hourUpdate;
        this.urlDailyJson = urlDailyJson;
        this.localFileName = localFileName;
    }

    /**
     * Конструктор объекта
     *
     * @param context        - контекст приложения
     * @param urlDailyJson-  URL адрес источника курса валют
     * @param localFileName- имя файла , куда будет сохранятся выгрузка из URL адреса (источника курса валют )
     */
    public CbrDaily(Context context, String urlDailyJson, String localFileName) {
        this.context = context;
        this.urlDailyJson = urlDailyJson;
        this.localFileName = localFileName;
    }

    /**
     * Загрузка/Перезагрузка курса валют на устройстве
     *
     * @param reload - признак перезаписи данных
     * @throws IOException
     * @throws JSONException
     */
    public void loadDailyJson(boolean reload) throws IOException, JSONException {
        loadDailyJson(reload, null);
    }

    /**
     * Загрузка/Перезагрузка курса валют на устройстве
     *
     * @param reload           - признак перезаписи данных
     * @param callbackCbrDaily - функция которая будет вызвана  после получения данных
     * @throws IOException
     * @throws JSONException
     */
    public void loadDailyJson(boolean reload, CallbackJsonObject callbackCbrDaily) throws IOException, JSONException {
        fileJson = new File(context.getFilesDir(), localFileName);
        if (fileJson.exists() && (reload || ((fileJson.lastModified() + (hourUpdate * HOUR)) < System.currentTimeMillis()))) {
            fileJson.delete();
        }
        if (!fileJson.exists()) {
            this.callbackCbrDaily = callbackCbrDaily;
            new HttpRequest().getJsonObject(urlDailyJson, (JSONObject response) -> {
                if (testResponse(response)) {
                    saveJsonObject(fileJson, response);
                }
                if (callbackCbrDaily != null) {
                    callbackCbrDaily.call(response);
                }
            });
        } else {
            JSONObject saveResponse = loadJsonObject(fileJson);
            if (callbackCbrDaily != null) {
                callbackCbrDaily.call(saveResponse);
            }
        }
    }

    /***
     * Проверка отсутствия ошибки в URL запросе
     * @param response
     * @return
     * @throws JSONException
     */
    private boolean testResponse(JSONObject response) throws JSONException {
        if (response.has("error")
                && response.getJSONObject("error").has("ResponseCode")
                && response.getJSONObject("error").getInt("ResponseCode") != 200) {
            return false;
        }
        return true;
    }

    /**
     * Сохранение Полученного JSON объекта в файл на устройстве(возможно переписать на сохронение в БД)
     *
     * @param fileJson
     * @param obj
     * @throws IOException
     */
    private void saveJsonObject(File fileJson, JSONObject obj) throws IOException {
        FileWriter fileWriter = new FileWriter(fileJson);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(obj.toString());
        bufferedWriter.close();
    }

    /**
     * Получение сохраненного объекта в формате JSON
     *
     * @param fileJson
     * @return
     * @throws IOException
     * @throws JSONException
     */
    private JSONObject loadJsonObject(File fileJson) throws IOException, JSONException {
        FileReader fileReader = new FileReader(fileJson);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        fileReader.close();
        return new JSONObject(stringBuilder.toString());
    }

}
