package com.choam.polycache.PolyAPICalls;

import java.util.ArrayList;

public class PolyObject {
    private String name;
    private String authorName;
    private String assetURL;

    private static ArrayList<PolyObject> polyObjects = new ArrayList<>();

    public PolyObject(String name, String authorName, String assetURL) {
        if(name != null && !name.isEmpty()) {
            this.name = name;
        }
        if(authorName != null && !authorName.isEmpty()) {
            this.authorName = authorName;
        }
        if(assetURL != null && !assetURL.isEmpty()) {
            this.assetURL = assetURL;
        }
    }

    public String getName() {
        return name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAssetURL() {
        return assetURL;
    }

    public static void addToPolyObjectList(PolyObject polyObject) {
        polyObjects.add(polyObject);
    }

    public static ArrayList<PolyObject> getPolyObjects() {
        return polyObjects;
    }

}
