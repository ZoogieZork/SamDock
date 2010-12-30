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

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;


public class PrefsActivity extends PreferenceActivity {
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        LaunchTypePreference launchAppPref = (LaunchTypePreference)findPreference("launchType");
        
        updateLaunchAppPrefSummary(launchAppPref, launchAppPref.getComplexValue());
        launchAppPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof LaunchTypePreference.ComplexValue) {
                    updateLaunchAppPrefSummary((ListPreference)preference,
                        (LaunchTypePreference.ComplexValue)newValue);
                }
                return true;
            }
        });
        
        Preference launchTestPref = findPreference("launchTest");
        launchTestPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                launchApp();
                return true;
            }
        });
    }
    
    private void updateLaunchAppPrefSummary(ListPreference pref, LaunchTypePreference.ComplexValue value) {
        CharSequence summary = "";
        LaunchType type = value.getLaunchType();
        if (LaunchType.APP == type) {
            summary = value.getAppLabel();
        } else {
            LaunchType[] types = LaunchType.values();
            for (int i = 0; i < types.length; ++i) {
                if (types[i] == type) {
                    summary = pref.getEntries()[i];
                    break;
                }
            }
        }
        
        pref.setSummary(summary);
    }
    
    protected void launchApp() {
        startService(new Intent(this, PowerService.class));
    }
    
}
