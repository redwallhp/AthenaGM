package io.github.redwallhp.athenagm.modules;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.modules.broadcasts.BroadcastsModule;
import io.github.redwallhp.athenagm.modules.chat.ChatModule;
import io.github.redwallhp.athenagm.modules.deathMessage.DeathMessageModule;
import io.github.redwallhp.athenagm.modules.friendlyFire.FriendlyFireModule;
import io.github.redwallhp.athenagm.modules.kits.KitsModule;
import io.github.redwallhp.athenagm.modules.permissions.PermissionsModule;
import io.github.redwallhp.athenagm.modules.playerFreeze.PlayerFreezeModule;
import io.github.redwallhp.athenagm.modules.scoreboard.ScoreboardModule;
import io.github.redwallhp.athenagm.modules.spectator.SpectatorModule;
import io.github.redwallhp.athenagm.modules.worldBorder.WorldBorderModule;

import java.util.HashMap;

/**
 * Does what it says on the box: builds and loads modules,
 * registering their event listeners as it does so.
 */
public class ModuleLoader {


    /**
     * List of module classes to load
     */
    private static Class[] moduleClasses = {
            PermissionsModule.class,
            SpectatorModule.class,
            KitsModule.class,
            ScoreboardModule.class,
            FriendlyFireModule.class,
            BroadcastsModule.class,
            PlayerFreezeModule.class,
            DeathMessageModule.class,
            WorldBorderModule.class,
            ChatModule.class
    };


    private AthenaGM plugin;
    private HashMap<String, Module> modules;


    public ModuleLoader(AthenaGM plugin) {
        this.plugin = plugin;
        this.modules = new HashMap<String, Module>();
        load();
    }


    /**
     * Build and load modules, registering their events and stashing references for later use.
     * Modules are cast to their Module interface to make this sorcery work, but can be
     * recast to their original class later in order to access methods.
     */
    @SuppressWarnings("unchecked")
    public void load() {
        for (Class c : moduleClasses) {
            try {
                plugin.getLogger().info(String.format("Loading module: %s", c.getName()));
                Module module = (Module) c.getConstructor(AthenaGM.class).newInstance(plugin); //evil sorcery
                plugin.getServer().getPluginManager().registerEvents(module, plugin);
                modules.put(module.getModuleName(), module);
            } catch (Exception ex) {
                plugin.getLogger().warning(String.format("Error loading module '%s", c.getName()));
                ex.printStackTrace();
            }
        }
    }


    /**
     * Iterate the loaded modules and call the unload() method each one implements.
     */
    public void unload() {
        for (Module module : modules.values()) {
            module.unload();
        }
    }


    /**
     * Get the module with a specified name.
     * The returned object will be a Module, but you can recast it to the module's original class
     * in order to access its methods. e.g. casting the module found by getting "permissions"
     * to be a PermissionsModule will allow you to access its member methods.
     * @param name Name of the module, as returned by its getModuleName() method
     */
    public Module getModule(String name) {
        return modules.get(name);
    }


}
