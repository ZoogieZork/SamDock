/*
 * Copyright (C) 2011 Michael Imamura
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

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;


public class AppListAdapter implements ListAdapter {

    private CharSequence[] appListLabels;
    private AppEntry[] appListValues;
    
    private LayoutInflater inflater;
    
    public AppListAdapter(Context ctx, CharSequence[] appListLabels, AppEntry[] appListValues) {
        this.appListLabels = appListLabels;
        this.appListValues = appListValues;
        
        inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // Does nothing.
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // Does nothing.
    }

    @Override
    public int getCount() {
        return appListValues.length;
    }

    @Override
    public Object getItem(int position) {
        return appListValues.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView == null) {
            view = (TextView)inflater.inflate(R.layout.app_list_item, null);
        } else {
            view = (TextView)convertView;
        }
        
        view.setText(appListLabels[position]);
        
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return appListValues.length == 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

}
