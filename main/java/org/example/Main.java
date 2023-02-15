package org.example;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.commands.MMQuest;


public final class Main extends JavaPlugin {
    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true));
    }
    @Override
    public void onEnable() {
        MMQuest.setupQuests();  //creates Quest instances + hashmap
        CommandAPI.onEnable(this);
        MMQuest.register();  //registers /mmquest
    }
    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }
}