package cz.fit.tam;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

abstract public class TamActivity extends Activity {
    
    protected static SharedPreferences preferences;
    
    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }
    
}
