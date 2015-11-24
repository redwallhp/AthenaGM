package io.github.redwallhp.athenagm.regions;


import java.util.HashMap;

/**
 * Handles the storage and retrieval of region flags
 */
public class RegionFlags {


    private HashMap<String, Boolean> bFlags;
    private HashMap<String, String> sFlags;
    private HashMap<String, Double> dFlags;


    public RegionFlags() {
        bFlags = new HashMap<String, Boolean>();
        sFlags = new HashMap<String, String>();
        dFlags = new HashMap<String, Double>();
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
     * Get a double flag
     */
    public Double getDouble(String flag) {
        if (dFlags.get(flag) == null) {
            return null;
        } else {
            return dFlags.get(flag);
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
     * Set a double flag
     */
    public void setDouble(String flag, Double value) {
        dFlags.put(flag, value);
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


    /**
     * Used so getApplicableRegion() can iterate double flags and merge
     * flags over applicable regions.
     */
    public HashMap<String, Double> getAllDoubleFlags() {
        return dFlags;
    }


}
