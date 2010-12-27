package org.lugatgt.org.zoogie.samdock;


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
