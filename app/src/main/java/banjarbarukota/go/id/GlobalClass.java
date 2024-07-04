package banjarbarukota.go.id;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.Preference;

import banjarbarukota.go.id.idaman.MainLoginActivity;

/**
 * Created by Wahyu on 5/9/2018.
 */

public class GlobalClass extends Application {
    public  SharedPreferences preferenceSetting;
    public  SharedPreferences.Editor preferenceEditor;

    private final int PREFERENCE_MODE_PRIVATE = 0;
    private String MY_PREFS_NAME = "pref";

    public String getStringPref(String key){
        String vHasil = null;
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //String restoredText = prefs.getString("text", null);
        //if (restoredText != null) {
            vHasil = prefs.getString(key, null);//"No name defined" is the default value.
           // int idName = prefs.getInt("idName", 0); //0 is the default value.
        //}
        return vHasil;
    }

    public void saveStringPref(String key, String value){
        preferenceEditor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        preferenceEditor.putString(key, value);
        preferenceEditor.commit();
    }
}
