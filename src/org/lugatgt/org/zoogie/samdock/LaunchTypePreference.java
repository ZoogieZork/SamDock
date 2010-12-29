/*
 * Copyright (C) 2010 Michael Imamura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lugatgt.org.zoogie.samdock;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.ListPreference;
import android.util.AttributeSet;


/**
 * Customized {@link ListPreference} for setting the launch type.
 * @author Michael Imamura
 */
public class LaunchTypePreference extends ListPreference {

    private PackageManager packageManager;
    
    private CharSequence[] appListLabels;
    private AppEntry[] appListValues;
    private Dialog appListDlg;
    
    private String newLaunchType;
    private AppEntry newAppEntry;
    
    // CONSTRUCTORS ////////////////////////////////////////////////////////////
    
    public LaunchTypePreference(Context context) {
        this(context, null);
    }

    public LaunchTypePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        LaunchType[] launchValues = LaunchType.values();
        String[] launchValueValues = new String[launchValues.length];
        for (int i = 0; i < launchValues.length; ++i) {
            launchValueValues[i] = launchValues[i].getCode();
        }
        setEntryValues(launchValueValues);
    }
    
    // FIELD ACCESS ////////////////////////////////////////////////////////////
    
    public void setPackageManager(PackageManager packageManager) {
        this.packageManager = packageManager;
    }
    
    @Override
    protected boolean callChangeListener(Object newValue) {
        if (newValue instanceof String) {
            // The first dialog has been closed.
            if (LaunchType.APP.name().equals(newValue)) {
                // Don't call the changelistener just yet --
                // we need to ask the user what app they want.
                newLaunchType = (String)newValue;
                
                if (appListDlg == null) {
                    appListDlg = createAppListDialog();
                }
                newAppEntry = null;
                appListDlg.show();
                
                return false;
            } else {
                return super.callChangeListener(newValue);
            }
        } else {
            // The app list dialog has been closed.
            return super.callChangeListener(newValue);
        }
    }
    
    @Override
    protected boolean persistString(String value) {
        if (newAppEntry == null) {
            return super.persistString(value);
        } else {
            //TODO: Persist new app entry.
            return true;
        }
    }
    
    // APP LIST DIALOG /////////////////////////////////////////////////////////
    
    private List<ResolveInfo> findInstalledApps(PackageManager pkgMgr) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pkgMgr.queryIntentActivities(mainIntent, 0);
    }
    
    private void populateInstalledAppList() {
        if (appListLabels == null) {
            List<ResolveInfo> installedApps = findInstalledApps(packageManager);
            
            appListValues = new AppEntry[installedApps.size()];
            int i = 0;
            for (ResolveInfo info : installedApps) {
                appListValues[i++] = new AppEntry(packageManager, info);
            }
            Arrays.sort(appListValues);
            
            appListLabels = new CharSequence[appListValues.length];
            for (i = 0; i < appListValues.length; ++i) {
                appListLabels[i] = appListValues[i].getLabel();
            }
        }
    }
    
    private Dialog createAppListDialog() {
        populateInstalledAppList();
        
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext())
            .setTitle(getTitle())
            .setNegativeButton(getNegativeButtonText(), null)
            .setSingleChoiceItems(appListLabels, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectAppEntry(which);
                    dialog.dismiss();
                }
            });
        
        Dialog dlg = dlgBuilder.create();
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onDismissAppListDialog();
            }
        });
        return dlg;
    }
    
    protected void selectAppEntry(int index) {
        if (appListValues != null && index >= 0 && index <= appListValues.length) {
            newAppEntry = appListValues[index];
        }
    }
    
    protected void onDismissAppListDialog() {
        if (newAppEntry != null) {
            if (callChangeListener(newAppEntry)) {
                setValue(newLaunchType);
            }
        }
    }
    
    private static class AppEntry implements Comparable<AppEntry> {
        private String label;
        private ResolveInfo info;
        
        public AppEntry(PackageManager pkgMgr, ResolveInfo info) {
            this.info = info;
            this.label = info.loadLabel(pkgMgr).toString();
        }
        
        public String getLabel() {
            return label;
        }
        
        public ResolveInfo getInfo() {
            return info;
        }
        
        @Override
        public int compareTo(AppEntry another) {
            return label.compareToIgnoreCase(another.label);
        }
    }
    
}
