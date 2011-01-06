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
            Intent launchIntent = LaunchUtil.loadIntent(prefs);
            if (launchIntent == null) {
                Log.e(TAG, "No intent configured; aborting.");
            } else {
                Log.i(TAG, "Launching " + launchIntent);
                try {
                    startActivity(launchIntent);
                } catch (Exception ex) {
                    Log.e(TAG, "Unable to launch " + launchIntent + ": " + ex);
                }
            }
        }
        
        stopSelf();
        
        return START_NOT_STICKY;
    }
    
}
