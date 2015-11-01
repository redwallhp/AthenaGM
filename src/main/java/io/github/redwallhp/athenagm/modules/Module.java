package io.github.redwallhp.athenagm.modules;


import org.bukkit.event.Listener;

public interface Module extends Listener {

    String getModuleName();
    void unload();

}
