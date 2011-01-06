package org.lugatgt.org.zoogie.samdock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


public class LaunchUtil {

    private static final String TAG = "LaunchUtil";
    
    public static Intent loadIntent(SharedPreferences prefs) {
        LaunchType launchType = LaunchType.fromCode(prefs.getString("launchType", null));
        if (launchType == null) launchType = LaunchType.AUTO_CLOCK;
        
        switch (launchType) {
            case AUTO_CLOCK:
                return makeAutoClockIntent();
                
            case APP:
                String packageName = prefs.getString("launchType.packageName", "");
                String activityName = prefs.getString("launchType.activityName", "");
                return makeIntentForActivity(packageName, activityName);
                
            default:
                Log.e(TAG, "Unhandled launch type: " + launchType.name());
                return null;
        }
    }
    
    private static Intent makeAutoClockIntent() {
        //TODO: Query for the other pre-installed clock apps.
        return makeIntentForActivity("com.android.deskclock", "com.android.deskclock.DeskClock");
    }
    
    private static Intent makeIntentForActivity(String packageName, String className) {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
    
}
