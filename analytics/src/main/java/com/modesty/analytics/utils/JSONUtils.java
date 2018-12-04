package com.modesty.analytics.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JSONUtils {

    /***
     * Workaround for broken JSONObject.
     *
     *     JSONObject wat = new JSONObject("{\"k\":null}");
     *     assert("null".equals(wat.optString("k")));
     *
     * Just let that sink in for a sec. I'll wait.
     *
     * @param o a JSONObject
     * @param k a key
     */
    public static String optionalStringKey(JSONObject o, String k){
        try{
            if (o.has(k) && !o.isNull(k)) {
                return o.getString(k);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static JSONArray stringToJsonArray(String json){
        JSONArray jsonArray;

        try {
            if(!TextUtils.isEmpty(json)){
                jsonArray = new JSONArray(json);
            }else{
                jsonArray = new JSONArray();
            }
        } catch (JSONException e) {
            jsonArray = new JSONArray();
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static JSONObject stringToJsonObject(String json){
        JSONObject jsonObject;

        try {
            if(!TextUtils.isEmpty(json)){
                jsonObject = new JSONObject(json);
            }else{
                jsonObject = new JSONObject();
            }
        } catch (JSONException e) {
            jsonObject = new JSONObject();
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject copyFrom( JSONObject object ) {
        if ( object == null ) {
            return null;
        }
        Iterator< String > keys = object.keys();
        JSONObject shadow = new JSONObject();
        try {
            while ( keys.hasNext() ) {
                String key = keys.next();
                shadow.put( key, object.get( key ) );
            }
        } catch ( Exception e ) {
            return null;
        }
        return shadow;
    }

}
