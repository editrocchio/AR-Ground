package com.choam.arground.PolyAPICalls;

import java.util.ArrayList;

public class PolyObject {
    private String name;
    private String authorName;
    private String assetURL;
    private String thumbURL;

    private static ArrayList<PolyObject> polyObjects = new ArrayList<>();

    public PolyObject(String name, String authorName, String assetURL, String thumbURL) {
        if(name != null && !name.isEmpty()) {
            this.name = name;
        }
        if(authorName != null && !authorName.isEmpty()) {
            this.authorName = authorName;
        }
        if(assetURL != null && !assetURL.isEmpty()) {
            this.assetURL = assetURL;
        }
        if(thumbURL != null && !thumbURL.isEmpty()) {
            this.thumbURL = thumbURL;
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

    public String getThumbURL() {
        return thumbURL;
    }

    public static void addToPolyObjectList(PolyObject polyObject) {
        polyObjects.add(polyObject);
    }

    public static ArrayList<PolyObject> getPolyObjects() {
        return polyObjects;
    }

}
