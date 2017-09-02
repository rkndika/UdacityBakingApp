package com.rkndika.udacitybakingapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.rkndika.udacitybakingapp.model.Recipe;

public class WidgetSharedPreference {
    // Sharedpref file name
    private static final String PREF_NAME = "WidgetDataPref";
    // Key for data
    public static final String KEY_RECIPE = "recipe";
    // Shared pref mode
    private static final int PRIVATE_MODE = 0;
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context context;

    // Constructor
    public WidgetSharedPreference(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveData(Recipe recipe){
        Gson gson = new Gson();
        String json = gson.toJson(recipe);
        editor.putString(KEY_RECIPE, json);
        editor.commit();
    }

    public Recipe getData(){
        Gson gson = new Gson();
        String json = pref.getString(KEY_RECIPE, null);
        return gson.fromJson(json, Recipe.class);
    }

    public void resetData(){
        editor.clear();
        editor.commit();
    }
}
