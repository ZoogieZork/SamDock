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
