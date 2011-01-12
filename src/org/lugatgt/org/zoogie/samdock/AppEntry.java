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

package org.lugatgt.org.zoogie.samdock;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;


public class AppEntry {
    private String label;
    private Drawable icon;
    private ResolveInfo info;
    
    public AppEntry(PackageManager pkgMgr, ResolveInfo info) {
        this.info = info;
        
        icon = info.loadIcon(pkgMgr);
        label = info.loadLabel(pkgMgr).toString();
    }
    
    public String getLabel() {
        return label;
    }
    
    public Drawable getIcon() {
        return icon;
    }
    
    public ResolveInfo getInfo() {
        return info;
    }
}
