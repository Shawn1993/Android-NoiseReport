package com.noiselab.noisecomplain.utility;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;

/**
 * Created by shawn on 26/3/2016.
 */
public class HttpUtil {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient();

    public static void postAsyncForm(String url, Object object, Callback callback) {
        try {
            Field[] fields = object.getClass().getFields();
            FormBody.Builder builder = new FormBody.Builder();
            Gson gson = new Gson();
            for (Field field : fields) {
                String value;
                Log.e(field.getType().toString(), "test");
                if (field.getType().isArray()) {
                    value = gson.toJson(field.get(object));
                } else {
                    value = String.valueOf(field.get(object));
                }
                Log.v(field.getName(), value + "");
                if (value != null) {
                    builder.add(field.getName(), value);
                }

            }
            postAsync(url, builder.build(), callback);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void postAsyncJson(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        postAsync(url, body, callback);
    }

    public static void postAsync(String url, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        if (callback == null) {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        } else {
            client.newCall(request).enqueue(callback);
        }
    }

    public static void getAsync(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
