package org.lugatgt.org.zoogie.samdock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PowerService extends Service {

    private static final String TAG = "PowerService";
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        Log.i(TAG, "Launching com.android.deskclock.AlarmClock");
        
        Intent clockIntent = new Intent();
        clockIntent.setClassName("com.android.deskclock", "com.android.deskclock.DeskClock");
        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(clockIntent);
        
        stopSelf();
        
        return START_NOT_STICKY;
    }

}
