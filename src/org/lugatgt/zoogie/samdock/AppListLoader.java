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

package org.lugatgt.zoogie.samdock;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.util.Log;


/**
 * Asynchronous app list loader, with progress dialog.
 * @author Michael Imamura
 */
public class AppListLoader extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "AppListLoader";
    
    private PackageManager pkgMgr;
    
    private CharSequence[] appListLabels;
    private AppEntry[] appListValues;
    
    private ProgressDialog progressDlg;
    
    // CONSTRUCTORS ////////////////////////////////////////////////////////////
    
    public AppListLoader(Context ctx) {
        pkgMgr = ctx.getPackageManager();
        
        progressDlg = new ProgressDialog(ctx);
        progressDlg.setIndeterminate(true);
        progressDlg.setMessage(ctx.getString(R.string.app_list_loader_message));
        progressDlg.setCancelable(true);
        progressDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(false);
            }
        });
        progressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel(false);
            }
        });
    }
    
    // FIELD ACCESS ////////////////////////////////////////////////////////////
    
    public CharSequence[] getAppListLabels() {
        return appListLabels;
    }

    public AppEntry[] getAppListValues() {
        return appListValues;
    }
    
    // LIFECYCLE ///////////////////////////////////////////////////////////////
    
    @Override
    protected void onPreExecute() {
        progressDlg.show();
    }
    
    @Override
    protected Void doInBackground(Void... params) {
        long startTime, diffTime;
        
        startTime = System.currentTimeMillis();
        List<ResolveInfo> installedApps = findInstalledApps();
        diffTime = System.currentTimeMillis() - startTime;
        Log.d(TAG, "Retrieved app list: " + diffTime + "ms");
        
        if (isCancelled()) return null;
        
        // Create the app entries.
        // The AppEntry constructor does the work of extracting the useful info.
        
        startTime = System.currentTimeMillis();
        appListValues = new AppEntry[installedApps.size()];
        int i = 0;
        for (ResolveInfo info : installedApps) {
            appListValues[i++] = new AppEntry(pkgMgr, info);
            
            if (isCancelled()) return null;
        }
        diffTime = System.currentTimeMillis() - startTime;
        Log.d(TAG, "Extracted app info: " + diffTime + "ms");
        
        // Sort the app list by name.
        
        final Comparator<ResolveInfo> riComp =
            new ResolveInfo.DisplayNameComparator(pkgMgr);
        
        startTime = System.currentTimeMillis();
        Arrays.sort(appListValues, new Comparator<AppEntry>() {
            @Override
            public int compare(AppEntry a, AppEntry b) {
                return riComp.compare(a.getInfo(), b.getInfo());
            }
        });
        diffTime = System.currentTimeMillis() - startTime;
        Log.d(TAG, "Sorted apps: " + diffTime + "ms");
        
        if (isCancelled()) return null;
        
        appListLabels = new CharSequence[appListValues.length];
        for (i = 0; i < appListValues.length; ++i) {
            appListLabels[i] = appListValues[i].getLabel();
        }
        
        return null;
    }
    
    @Override
    protected void onPostExecute(Void result) {
        progressDlg.dismiss();
    }
    
    // UTILITIES ///////////////////////////////////////////////////////////////

    private List<ResolveInfo> findInstalledApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pkgMgr.queryIntentActivities(mainIntent, 0);
    }

}
