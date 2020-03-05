package org.xjcraft.hackerchecker;

import org.xjcraft.CommonPlugin;
import org.xjcraft.hackerchecker.config.LogConfig;
import org.xjcraft.hackerchecker.config.WarningConfig;

public final class HackerChecker extends CommonPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfigs();
        PlayerListener playerListener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        registerCommand(playerListener);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig(LogConfig.class);
        saveConfig(WarningConfig.class);
    }
}
