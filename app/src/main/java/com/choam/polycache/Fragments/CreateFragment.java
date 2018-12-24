package com.choam.polycache.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.choam.polycache.BuildConfig;
import com.choam.polycache.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateFragment extends Fragment {

    private static final String API_KEY = BuildConfig.ApiKeyDebugPoly;
    private static final String BASE_URL = "https://poly.googleapis.com/v1";
    private static final String TAG = "CreateFragment";
    private ReceiveFeedTask receiveFeedTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create, container, false);

        Button send = view.findViewById(R.id.btnSend);
        receiveFeedTask = new ReceiveFeedTask();

        send.setOnClickListener(v -> {
            receiveFeedTask.execute(BASE_URL);
        });

        return view;
    }


    private static class ReceiveFeedTask extends AsyncTask<String, Void, String> {
        JSONObject res;
        JSONArray assets;

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient httpClient = new OkHttpClient();
            String url =  params[0] + "/assets/";

            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
            httpBuilder.addQueryParameter("key", API_KEY);
            httpBuilder.addQueryParameter("category", "animals");
            httpBuilder.addQueryParameter("format", "OBJ");

            Request request = new Request.Builder().url(httpBuilder.build()).build();

            Response response;
            try {
                response = httpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                Log.d(TAG, "error getting assets");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                res = new JSONObject(result);
                assets = res.getJSONArray("assets");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                for(int i=0; i<assets.length(); i++) {
                    Log.d(TAG, "ID: " + assets.getJSONObject(i).getString("name") + "\n" +
                            "DISPLAY NAME: " + assets.getJSONObject(i).getString("displayName") + "\n");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
