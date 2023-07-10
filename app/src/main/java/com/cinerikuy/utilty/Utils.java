package com.cinerikuy.utilty;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    private static final String TAG_BEGIN = "Request: ";
    private static final String TAG_END = "Response: ";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void logRequest(Object object){

        try{
            String json = MAPPER.writeValueAsString(object);
            Log.d(TAG_BEGIN, json);
        } catch (JsonProcessingException e) {
            Log.e(TAG_BEGIN, "Error converting object to JSON: " + e.getMessage());
        }
    }

    public static void logResponse(Object object){

        try{
            String json = MAPPER.writeValueAsString(object);
            Log.d(TAG_END, json);
        } catch (JsonProcessingException e) {
            Log.e(TAG_END, "Error converting object to JSON: " + e.getMessage());
        }
    }
}
