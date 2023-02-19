package org.example;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.commands.MMQuest;

import java.io.IOException;



public final class Main extends JavaPlugin {
    private static Main INSTANCE;
    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true));
    }
    @Override
    public void onEnable() {
        INSTANCE = this;
        try {
            // Parses JSON file to create Quest instances, throws error if no file found.
            MMQuest.setupQuests();
        } catch (IOException e) {
            // Could either log a message to console or to opped players
        }
        CommandAPI.onEnable(this);
        MMQuest.register();  //registers /mmquest
    }
    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }

    public static Main getINSTANCE() {
        return INSTANCE;
    }
}