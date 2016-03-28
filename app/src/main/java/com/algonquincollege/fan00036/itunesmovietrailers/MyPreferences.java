package com.algonquincollege.fan00036.itunesmovietrailers;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.SharedPreferences.*;

/**
 * Created by helen on 2015-12-07.
 */
public class MyPreferences {
    private SharedPreferences preferences;
    private Editor editor;
    private static String MY_PREFS="MY_PREFS";
    private static String LAST_BUILD_DATE="LastBuildDate";

    public MyPreferences(Context context)
    {
        preferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String getLastBuildDate() {
        return preferences.getString(LAST_BUILD_DATE, "");
    }
    public void setLastBuildDate(String lastBuildDate) {
        editor.putString(LAST_BUILD_DATE, lastBuildDate);
        editor.commit();
    }
    public void removeLastBuildDate() {
        editor.remove(LAST_BUILD_DATE);
        editor.commit();
    }
}
