package com.modesty.logger.file;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

class JSONUtils {
    JSONUtils() {
    }

    public static JSONArray stringToJsonArray(String json) {
        JSONArray jsonArray;
        try {
            if(!TextUtils.isEmpty(json)) {
                jsonArray = new JSONArray(json);
            } else {
                jsonArray = new JSONArray();
            }
        } catch (JSONException var3) {
            jsonArray = new JSONArray();
            var3.printStackTrace();
        }

        return jsonArray;
    }

    public static JSONObject stringToJsonObject(String json) {
        JSONObject jsonObject;
        try {
            if(!TextUtils.isEmpty(json)) {
                jsonObject = new JSONObject(json);
            } else {
                jsonObject = new JSONObject();
            }
        } catch (JSONException var3) {
            jsonObject = new JSONObject();
            var3.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject copyFrom(JSONObject object) {
        if(object == null) {
            return null;
        } else {
            Iterator<String> keys = object.keys();
            JSONObject shadow = new JSONObject();

            try {
                while(keys.hasNext()) {
                    String key = (String)keys.next();
                    shadow.put(key, object.get(key));
                }

                return shadow;
            } catch (Exception var4) {
                return null;
            }
        }
    }
}
