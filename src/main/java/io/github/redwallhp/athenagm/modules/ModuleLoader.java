package io.github.redwallhp.athenagm.modules;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.modules.permissions.PermissionsModule;

import java.util.HashMap;

public class ModuleLoader {


    private static Class[] moduleClasses = {
            PermissionsModule.class
    };


    private AthenaGM plugin;
    private HashMap<String, Module> modules;


    public ModuleLoader(AthenaGM plugin) {
        this.plugin = plugin;
        this.modules = new HashMap<String, Module>();
        load();
    }


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


    public void unload() {
        for (Module module : modules.values()) {
            module.unload();
        }
    }


    public Module getModule(String name) {
        return modules.get(name);
    }


}
