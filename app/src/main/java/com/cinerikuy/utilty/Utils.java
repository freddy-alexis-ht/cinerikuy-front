package com.cinerikuy.utilty;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public static String convertDateFormat(String fecha) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - h:mm a", Locale.getDefault());
        String newFormatFecha ="";
        try {
            Date date = inputFormat.parse(fecha);
            newFormatFecha = outputFormat.format(date);

        } catch (Exception e) {
            Log.e("DateFormat", e.getMessage());
        }
        return  newFormatFecha;
    }
}
