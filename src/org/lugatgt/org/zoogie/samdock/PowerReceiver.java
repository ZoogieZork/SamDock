package org.lugatgt.org.zoogie.samdock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PowerReceiver extends BroadcastReceiver {

    private static final String TAG = "PowerReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Starting PowerService");
        context.startService(new Intent(context, PowerService.class));
    }

}
