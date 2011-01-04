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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class PrefsActivity extends PreferenceActivity {
    
    private static final String TAG = "PrefsActivity";
    
    private static final int DLG_LAUNCH_DETAILS = 1;
    
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prefs_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.launch_details:
                showDialog(DLG_LAUNCH_DETAILS);
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DLG_LAUNCH_DETAILS: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("")  // Necessary to initialize the view.
                    .setNegativeButton(R.string.launch_details_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                return builder.create();
            }
                
            default:
                Log.e(TAG, "Unknown dialog ID to create: " + id);
                return null;
        }
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dlg) {
        switch (id) {
            case DLG_LAUNCH_DETAILS: {
                AlertDialog alertDlg = (AlertDialog)dlg;
                alertDlg.setMessage(getString(R.string.launch_details_msg,
                    "Title",
                    "Package",
                    "Class"));
                break;
            }
            
            default:
                Log.e(TAG, "Unknown dialog ID to prepare: " + id);
        }
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
