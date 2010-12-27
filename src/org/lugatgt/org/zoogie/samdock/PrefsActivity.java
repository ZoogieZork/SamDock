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

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity {
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        ListPreference launchAppPref = (ListPreference)findPreference("launchType");
        
        LaunchType[] launchValues = LaunchType.values();
        String[] launchValueValues = new String[launchValues.length];
        for (int i = 0; i < launchValues.length; ++i) {
            launchValueValues[i] = launchValues[i].getCode();
        }
        launchAppPref.setEntryValues(launchValueValues);
        
        updateLaunchAppPrefSummary(launchAppPref, launchAppPref.getValue());
        launchAppPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateLaunchAppPrefSummary((ListPreference)preference, newValue.toString());
                if (LaunchType.APP.getCode().equals(newValue)) {
                    //TODO: Display the dialog to select the app.
                }
                return true;
            }
        });
    }
    
    private List<ResolveInfo> findInstalledApps(PackageManager pkgMgr) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pkgMgr.queryIntentActivities(mainIntent, 0);
    }
    
    private void populateInstalledAppList(ListPreference pref) {
        PackageManager pkgMgr = getBaseContext().getPackageManager();
        List<ResolveInfo> installedApps = findInstalledApps(pkgMgr);
        CharSequence[] names = new String[installedApps.size()];
        CharSequence[] values = new String[installedApps.size()];
        int i = 0;
        for (ResolveInfo info : installedApps) {
            names[i] = info.loadLabel(pkgMgr);
            values[i] = info.activityInfo.packageName;
            i++;
        }
        pref.setEntries(names);
        pref.setEntryValues(values);
    }
    
    private void updateLaunchAppPrefSummary(ListPreference pref, String value) {
        CharSequence summary = "";
        if (LaunchType.APP.name().equals(value)) {
            //TODO: Use the name.
            summary = "(App name here)";
        } else {
            LaunchType[] types = LaunchType.values();
            for (int i = 0; i < types.length; ++i) {
                if (types[i].getCode().equals(value)) {
                    summary = pref.getEntries()[i];
                    break;
                }
            }
        }
        
        pref.setSummary(summary);
    }
    
}
