package com.choam.polycache.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.choam.polycache.AssetAdapter;
import com.choam.polycache.BuildConfig;
import com.choam.polycache.PolyObject;
import com.choam.polycache.PopulateAssetList;
import com.choam.polycache.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateFragment extends Fragment {

    private static final String API_KEY = BuildConfig.ApiKeyDebugPoly;
    private static final String BASE_URL = "https://poly.googleapis.com/v1";
    private static final String TAG = "CreateFragment";
    private ReceiveFeedTask receiveFeedTask;
    private EditText catEditTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create, container, false);

        Button send = view.findViewById(R.id.btnSend);
        catEditTxt = view.findViewById(R.id.category);

        send.setOnClickListener(v -> {
            if(catEditTxt.getText() != null && !catEditTxt.getText().toString().isEmpty()) {
                receiveFeedTask = new ReceiveFeedTask(view.getContext(), view);
                receiveFeedTask.execute(catEditTxt.getText().toString());

            }
        });


        return view;
    }

    private static class ReceiveFeedTask extends AsyncTask<String, Void, String> {
        private WeakReference<Context> context;
        private WeakReference<View> view;
        private JSONArray assets;

        private ReceiveFeedTask(Context context, View view) {
            this.context = new WeakReference<>(context);
            this.view = new WeakReference<>(view);
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient httpClient = new OkHttpClient();
            String url =  BASE_URL + "/assets/";

            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
            httpBuilder.addQueryParameter("key", API_KEY);
            httpBuilder.addQueryParameter("category", params[0]);
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
            PolyObject.getPolyObjects().clear();
            try {
                JSONObject res = new JSONObject(result);
                assets = res.getJSONArray("assets");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                for(int i=0; i<assets.length(); i++) {
                    String name = assets.getJSONObject(i).getString("displayName");
                    String authorName = assets.getJSONObject(i).getString("authorName");
                    String assetURL = assets.getJSONObject(i).getString("name");

                    PolyObject.addToPolyObjectList(new PolyObject(name, authorName, assetURL));


                    Log.d(TAG, "ID: " + assets.getJSONObject(i).getString("name") + "\n" +
                            "DISPLAY NAME: " + assets.getJSONObject(i).getString("displayName") + "\n");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException n) {
                Log.d(TAG, "doesn't exist");
            }

            View v = view.get();
            Context c = context.get();

            v.getContext().startActivity(new Intent(c, PopulateAssetList.class));

        }

    }

}
