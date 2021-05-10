package tcking.github.com.giraffeplayer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 8/23/2017.
 */

//SharedPreferences manager class
public class SharedPref {

    //SharedPreferences file name
    private static String SHARED_PREFS_FILE_NAME = "video_app_shared_prefs";

    //here you can centralize all your shared prefs keys
    public static String KEY_MY_SHARED_BOOLEAN = "my_shared_boolean";
    public static String KEY_MY_SHARED_FOO = "my_shared_foo";

    public static String PREF_VIDEO_LIST = "songs_list";
    public static String PREF_CURRENT_PLAY_INDEX = "current_play_index";
    public static String PREF_CURRENT_SEEK_POSITION = "current_seek_bar_position";
    public static String PREF_IS_FLOATING_SCREEN = "is_floating_window";
    //get the SharedPreferences object instance
    //create SharedPreferences file if not present


    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

    //Save Booleans
    public static void save(Context context, String key, boolean value) {
        getPrefs(context).edit().putBoolean(key, value).commit();
    }

    //Get Booleans
    public static boolean getBoolean(Context context, String key) {
        return getPrefs(context).getBoolean(key, false);
    }

    //Get Booleans if not found return a predefined default value
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getPrefs(context).getBoolean(key, defaultValue);
    }

    //Strings
    public static void save(Context context, String key, String value) {
        getPrefs(context).edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key) {
        return getPrefs(context).getString(key, "");
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getPrefs(context).getString(key, defaultValue);
    }

    //Integers
    public static void save(Context context, String key, int value) {
        getPrefs(context).edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key) {
        return getPrefs(context).getInt(key, 0);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getPrefs(context).getInt(key, defaultValue);
    }

    //Floats
    public static void save(Context context, String key, float value) {
        getPrefs(context).edit().putFloat(key, value).commit();
    }

    public static float getFloat(Context context, String key) {
        return getPrefs(context).getFloat(key, 0);
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        return getPrefs(context).getFloat(key, defaultValue);
    }

    //Longs
    public static void save(Context context, String key, long value) {
        getPrefs(context).edit().putLong(key, value).commit();
    }

    public static long getLong(Context context, String key) {
        return getPrefs(context).getLong(key, 0);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        return getPrefs(context).getLong(key, defaultValue);
    }


    public static void setVideoList(List<SongModel> list, Context context) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        getPrefs(context).edit().putString(PREF_VIDEO_LIST, json).commit();


    }

    public static List<SongModel> getVideoList(Context context) {
        Gson gson = new Gson();
        String json = getPrefs(context).getString(PREF_VIDEO_LIST, null);
        Type type = new TypeToken<ArrayList<SongModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

}