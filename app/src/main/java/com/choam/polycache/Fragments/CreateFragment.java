package com.choam.polycache.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.choam.polycache.BuildConfig;
import com.choam.polycache.PolyAPICalls.PolyObject;
import com.choam.polycache.PolyAPICalls.PopulateAssetList;
import com.choam.polycache.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateFragment extends Fragment  {

    public static final String API_KEY = BuildConfig.ApiKeyDebugPoly;
    public static final String BASE_URL = "https://poly.googleapis.com/v1/";
    private static final String TAG = "CreateFragment";
    private ReceiveFeedTask receiveFeedTask;
    private EditText catEditTxt;
    private static String selectedCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create, container, false);

        Button send = view.findViewById(R.id.btnSend);
        catEditTxt = view.findViewById(R.id.search_assets);
        Spinner spinner = view.findViewById(R.id.category_spinner);
        selectedCategory = "";

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });

        send.setOnClickListener(v -> {
            receiveFeedTask = new ReceiveFeedTask(view.getContext(), view);
            receiveFeedTask.execute(catEditTxt.getText().toString());
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
            String url =  BASE_URL + "assets/";

            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

            //If the cat is all and they put a search term.
            if(selectedCategory.toLowerCase().equals("all") && params[0] != null && !params[0].isEmpty()) {
                httpBuilder.addQueryParameter("key", API_KEY);
                httpBuilder.addQueryParameter("keywords", params[0].toLowerCase());
                httpBuilder.addQueryParameter("format", "OBJ");
              //if they choose a cat and enter a search term
            } else if(!selectedCategory.toLowerCase().equals("all") && params[0] != null && !params[0].isEmpty()){
                httpBuilder.addQueryParameter("key", API_KEY);
                httpBuilder.addQueryParameter("category", selectedCategory.toLowerCase());
                httpBuilder.addQueryParameter("keywords", params[0].toLowerCase());
                httpBuilder.addQueryParameter("format", "OBJ");
              //if they chose a cat but left the search box empty
            } else if(!selectedCategory.toLowerCase().equals("all") && (params[0] == null || params[0].isEmpty())) {
                httpBuilder.addQueryParameter("key", API_KEY);
                httpBuilder.addQueryParameter("category", selectedCategory.toLowerCase());
                httpBuilder.addQueryParameter("format", "OBJ");
              //if the cat is all and they didn't enter a search term
            } else if(selectedCategory.toLowerCase().equals("all") && (params[0] == null || params[0].isEmpty())) {
                httpBuilder.addQueryParameter("key", API_KEY);
                httpBuilder.addQueryParameter("format", "OBJ");
            }

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
            View v = view.get();
            Context c = context.get();
        //    Log.d(TAG, result);
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
                    String assetURL = BASE_URL + assets.getJSONObject(i).getString("name");
                    String thumbURL = assets.getJSONObject(i).getJSONObject("thumbnail").getString("url");

                    PolyObject.addToPolyObjectList(new PolyObject(name, authorName, assetURL, thumbURL));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException n) {
                Toast.makeText(c, "Nothing found", Toast.LENGTH_SHORT).show();
            }

            if(PolyObject.getPolyObjects().size() > 0) {
                v.getContext().startActivity(new Intent(c, PopulateAssetList.class));
            }

        }

    }

}
