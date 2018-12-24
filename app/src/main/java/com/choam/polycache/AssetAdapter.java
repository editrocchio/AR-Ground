package com.choam.polycache;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class AssetAdapter extends
        RecyclerView.Adapter<AssetAdapter.ViewHolder> {

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
        Button button = viewHolder.chooseButton;
        button.setText("Select " + polyObject.getName());
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



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.asset_name);
            chooseButton = itemView.findViewById(R.id.choose_button);

        }


    }
}