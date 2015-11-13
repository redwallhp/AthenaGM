package io.github.redwallhp.athenagm.modules;


import org.bukkit.event.Listener;

/**
 * The Module interface that all modules must implement
 */
public interface Module extends Listener {


    /**
     * A string identifier for the module, to be used for getting instances with
     * the getModule() method.
     * @see ModuleLoader
     */
    String getModuleName();


    /**
     * Called when the plugin unloads. This is a good place to clean up database connections, etc.
     */
    void unload();


}
