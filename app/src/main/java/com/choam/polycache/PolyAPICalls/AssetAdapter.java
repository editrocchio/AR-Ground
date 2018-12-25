package com.choam.polycache.PolyAPICalls;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.choam.polycache.Fragments.CreateFragment;
import com.choam.polycache.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<PolyObject> polyObjects;

    public AssetAdapter(List<PolyObject> polyObjects) {
        this.polyObjects = polyObjects;
    }


    // inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public AssetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View assetView = layoutInflater.inflate(R.layout.item_asset, viewGroup, false);
        return new ViewHolder(assetView);
    }

    //populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull AssetAdapter.ViewHolder viewHolder, int i) {
        // Get the data model based on position
        PolyObject polyObject = polyObjects.get(i);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(polyObject.getName());
        Button chooseButton = viewHolder.chooseButton;
        Button viewButton = viewHolder.viewButton;

        viewButton.setOnClickListener(v -> {
            GetAssetTask getAssetTask = new GetAssetTask();
            getAssetTask.execute(polyObject.getAssetURL());
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return polyObjects.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView nameTextView;
        private Button chooseButton;
        private Button viewButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.asset_name);
            chooseButton = itemView.findViewById(R.id.choose_button);
            viewButton = itemView.findViewById(R.id.view_button);
        }

    }

    private static class GetAssetTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "GetAssetTask";

        private JSONArray formats;
        private String objFileUrl;
        private String mtlFileUrl;
        private String mtlFileName;

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient httpClient = new OkHttpClient();
            String url = params[0];

            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
            httpBuilder.addQueryParameter("key", CreateFragment.API_KEY);
            Request request = new Request.Builder().url(httpBuilder.build()).build();

            try {
                Response response = httpClient.newCall(request).execute();
                Log.d(TAG, response.body().string());
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject res = new JSONObject(result);
                formats = res.getJSONArray("formats");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                for (int i = 0; i < formats.length(); i++) {
                    JSONObject currentFormat = formats.getJSONObject(i);
                    if(currentFormat.getString("formatType").equals("OBJ")) {
                        //get .obj file details
                        objFileUrl = currentFormat.getJSONObject("root").getString("url");
                        //get .mtl file details
                        mtlFileUrl = currentFormat.getJSONArray("resources")
                                .getJSONObject(0)
                                .getString("url");

                        mtlFileName = currentFormat.getJSONArray("resources")
                                .getJSONObject(0)
                                .getString("relativePath");

                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}