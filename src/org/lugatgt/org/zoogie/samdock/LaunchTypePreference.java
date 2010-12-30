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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;


/**
 * Customized {@link ListPreference} for setting the launch type.
 * @author Michael Imamura
 */
public class LaunchTypePreference extends ListPreference {

    private static final String TAG = "LaunchTypePreference";
    
    private String appLabelKey;
    private String appPackageNameKey;
    private String appActivityNameKey;
    
    private CharSequence[] appListLabels;
    private AppEntry[] appListValues;
    private Dialog appListDlg;
    
    private ComplexValue complexValue;
    
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
        
        String key = getKey();
        appLabelKey = key + ".label";
        appPackageNameKey = key + ".packageName";
        appActivityNameKey = key + ".activityName";
    }
    
    // FIELD ACCESS ////////////////////////////////////////////////////////////
    
    public ComplexValue getComplexValue() {
        return complexValue;
    }
    
    // ListPreference //////////////////////////////////////////////////////////
    
    @Override
    protected boolean callChangeListener(Object newValue) {
        if (newValue instanceof String) {
            // The first dialog has been closed.
            if (LaunchType.APP.name().equals(newValue)) {
                // Don't call the changelistener just yet --
                // we need to ask the user what app they want.
                newLaunchType = (String)newValue;
                
                // If the app list dialog hasn't been created yet, then
                // load the list asynchronously and show the dialog when
                // the task completes.
                if (appListDlg == null) {
                    new AppListLoader(getContext()) {
                        @Override
                        protected void onPostExecute(Void result) {
                            if (!isCancelled()) {
                                appListLabels = getAppListLabels();
                                appListValues = getAppListValues();
                                
                                appListDlg = createAppListDialog();
                                showAppListDialog();
                            }
                            
                            super.onPostExecute(result);
                        }
                    }.execute((Void[])null);
                } else {
                    showAppListDialog();
                }
                
                return false;
            } else {
                return super.callChangeListener(new ComplexValue(
                    LaunchType.fromCode((String)newValue)));
            }
        } else {
            // The app list dialog has been closed.
            return super.callChangeListener(newValue);
        }
    }
    
    @Override
    protected boolean persistString(String value) {
        //HACK: Should refactor some of this into setComplexValue().
        if (newAppEntry == null) {
            return super.persistString(value);
        } else {
            ComplexValue newComplexValue =
                new ComplexValue(LaunchType.fromCode(value),
                    newAppEntry.getLabel(),
                    newAppEntry.getInfo().activityInfo.packageName,
                    newAppEntry.getInfo().activityInfo.name);
            
            // Persist new app entry, along with the launch type.
            SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
            editor.putString(getKey(), value);
            editor.putString(appLabelKey, newComplexValue.getAppLabel());
            editor.putString(appPackageNameKey, newComplexValue.getAppPackageName());
            editor.putString(appActivityNameKey, newComplexValue.getAppActivityName());
            if (!editor.commit()) {
                Log.e(TAG, "Failed to commit preferences: " + getKey());
            } else {
                complexValue = newComplexValue;
                Log.i(TAG, "Selected activity is: " + complexValue);
            }
            
            return true;
        }
    }
    
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);
        
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        String appLabel = prefs.getString(appLabelKey, "");
        String appPackageName = prefs.getString(appPackageNameKey, "");
        String appActivityName = prefs.getString(appActivityNameKey, "");
        complexValue = new ComplexValue(LaunchType.fromCode(getValue()),
            appLabel, appPackageName, appActivityName);
    }
    
    // APP LIST DIALOG /////////////////////////////////////////////////////////
    
    private Dialog createAppListDialog() {
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
    
    protected void showAppListDialog() {
        newAppEntry = null;
        appListDlg.show();
    }
    
    protected void selectAppEntry(int index) {
        if (appListValues != null && index >= 0 && index <= appListValues.length) {
            newAppEntry = appListValues[index];
        }
    }
    
    protected void onDismissAppListDialog() {
        if (newAppEntry != null) {
            ComplexValue newComplexValue =
                new ComplexValue(LaunchType.fromCode(newLaunchType),
                    newAppEntry.getLabel(),
                    newAppEntry.getInfo().activityInfo.packageName,
                    newAppEntry.getInfo().activityInfo.name);
            
            if (callChangeListener(newComplexValue)) {
                setValue(newLaunchType);
            }
        }
    }
    
    public static class ComplexValue {
        private LaunchType launchType;
        private String appLabel;
        private String appPackageName;
        private String appActivityName;
        
        public ComplexValue(LaunchType launchType) {
            this(launchType, "", "", "");
        }
        
        public ComplexValue(LaunchType launchType, String appLabel,
            String appPackageName, String appActivityName)
        {
            this.launchType = launchType;
            this.appLabel = appLabel;
            this.appPackageName = appPackageName;
            this.appActivityName = appActivityName;
        }
        
        public LaunchType getLaunchType() {
            return launchType;
        }
        
        public String getAppLabel() {
            return appLabel;
        }
        
        public String getAppPackageName() {
            return appPackageName;
        }

        public String getAppActivityName() {
            return appActivityName;
        }
        
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                "launchType=" + getLaunchType() + ", " +
                "appLabel=" + getAppLabel() + ", " +
                "appPackageName=" + getAppPackageName() + ", " +
                "appActivityName=" + getAppActivityName() +
                "}";
        }
    }
    
}
