package io.github.redwallhp.athenagm.regions;


import java.util.HashMap;

/**
 * Handles the storage and retrieval of region flags
 */
public class RegionFlags {


    private HashMap<String, Boolean> bFlags;
    private HashMap<String, String> sFlags;


    public RegionFlags() {
        bFlags = new HashMap<String, Boolean>();
        sFlags = new HashMap<String, String>();
    }


    /**
     * Get a boolean flag
     */
    public boolean getBoolean(String flag) {
        if (bFlags.get(flag) == null) return true;
        return bFlags.get(flag);
    }


    /**
     * Get a string flag
     */
    public String getString(String flag) {
        if (sFlags.get(flag) == null || sFlags.get(flag).equals("")) {
            return null;
        } else {
            return sFlags.get(flag);
        }
    }


    /**
     * Set a boolean flag
     */
    public void setBoolean(String flag, boolean value) {
        bFlags.put(flag, value);
    }


    /**
     * Set a string flag
     */
    public void setString(String flag, String value) {
        sFlags.put(flag, value);
    }


    /**
     * Used so getApplicableRegion() can iterate boolean flags and merge
     * flags over applicable regions.
     */
    public HashMap<String, Boolean> getAllBooleanFlags() {
        return bFlags;
    }


    /**
     * Used so getApplicableRegion() can iterate boolean flags and merge
     * flags over applicable regions.
     */
    public HashMap<String, String> getAllStringFlags() {
        return sFlags;
    }


}
