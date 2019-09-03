package ly.bsagar.newsapp;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class HelperFunctions {

    private static final String TAG = "HTTPRequest";

    static ArrayList<News> getNewsFromGaurdian(String stringUrl) throws JSONException, IOException {
        URL url = makeURL(stringUrl);
        if (url != null) {
            String responce = makeHTTPRequest(url);
            return makeArrayFromJSON(responce);
        } else {
            return null;
        }


    }

    private static URL makeURL(String string) {
        URL url = null;
        try {
            url = new URL(string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String makeHTTPRequest(URL url) throws IOException {
        String jsonResponce = null;

        if (url == null) {
            return jsonResponce;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponce = readFromInput(inputStream);
            } else {
                Log.d(TAG, "makeHTTPRequest: " + urlConnection.getResponseCode() +
                        urlConnection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponce;

    }

    private static String readFromInput(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
            BufferedReader buffer = new BufferedReader(reader);
            String line = buffer.readLine();
            while (line != null) {
                builder.append(line);
                line = buffer.readLine();
            }
        }
        return builder.toString();
    }

    static private ArrayList<News> makeArrayFromJSON(String jsonString) throws JSONException {
        if (jsonString == null) {
            return null;
        }

        ArrayList<News> newsList = new ArrayList<>();

        JSONArray array = new JSONObject(jsonString).
                getJSONObject("response").getJSONArray("results");

        for (int i = 0; i < array.length(); i++) {
            JSONObject currentObject = array.getJSONObject(i);
            Gson gson = new GsonBuilder().create();
            News currentNews = (News) gson.fromJson(currentObject.toString(), News.class);

            newsList.add(currentNews);
        }

        return newsList;


    }


}
