package com.gameblowmind.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    public static void savePoints(Context context, long points) {
        SharedPreferences sp = context.getSharedPreferences("points_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("point_key", points);
        editor.commit();
    }

    public static long getPoints(Context context) {
        SharedPreferences sp = context.getSharedPreferences("points_prefs", Activity.MODE_PRIVATE);
        return sp.getLong("point_key", 0);
    }
}
