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


public enum LaunchType {
    
    /** Automatically determine the clock app. */
    AUTO_CLOCK,
    
    /** User-selected app. */
    APP;
    
    /**
     * Retrieve the code for this launch type as stored in preferences.
     * @return The code (never null).
     */
    public String getCode() {
        return name();
    }
    
    /**
     * Retrieve the launch type for a given code.
     * @param code The code (may be null).
     * @return The launch type, or null if <tt>code</tt> is null or does not
     *         match a known code.
     */
    public static LaunchType fromCode(String code) {
        if (code == null) return null;
        
        try {
            return valueOf(code);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
}
