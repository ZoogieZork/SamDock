/*
 * Copyright (C) 2010 Michael Imamura
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.lugatgt.org.zoogie.samdock;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class PowerService extends Service {

    private static final String TAG = "PowerService";
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (prefs.getBoolean("enabled", true)) {
            LaunchType launchType = LaunchType.fromCode(prefs.getString("launchType", null));
            if (launchType == null) launchType = LaunchType.AUTO_CLOCK;
            
            switch (launchType) {
                case AUTO_CLOCK:
                    launchAutoClock();
                    break;
                    
                case APP:
                    String packageName = prefs.getString("launchType.packageName", "");
                    String activityName = prefs.getString("launchType.activityName", "");
                    launchActivity(packageName, activityName);
                    break;
                    
                default:
                    Log.e(TAG, "Unhandled launch type: " + launchType.name());
            }
        }
        
        stopSelf();
        
        return START_NOT_STICKY;
    }
    
    private void launchAutoClock() {
        //TODO: Query for the other pre-installed clock apps.
        
        launchActivity("com.android.deskclock", "com.android.deskclock.DeskClock");
    }
    
    private void launchActivity(String packageName, String className) {
        Log.i(TAG, "Launching " + packageName + '/' + className);
        
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, "Unable to launch " + packageName + '/' + className + ": " + ex);
        }
    }

}
