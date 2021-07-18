package com.nowinski.kamil.flighttracker.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class AirCraftImageUrlParser {
    public static String findAircraftImageUrl(String jsonData){
        JsonObject jsonObject = new JsonParser().parse(jsonData).getAsJsonObject();
        if(jsonObject.getAsJsonObject("aircraft").getAsJsonObject("images") != null) {
            return jsonData.substring(jsonData.indexOf("thumbnails") + 21, jsonData.indexOf(".jpg") + 4).replaceAll("/", "");
        }
        else {
            return "";
        }
    }
}
