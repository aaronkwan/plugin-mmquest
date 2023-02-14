package org.example;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.commands.MMQuest;


public final class Main extends JavaPlugin {
    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true)); // Load with verbose output

        new CommandAPICommand("ping")
                .executes((sender, args) -> {
                    sender.sendMessage("pong!");
                })
                .register();



    }
    @Override
    public void onEnable() {
        MMQuest.setupQuests();
        CommandAPI.onEnable(this);
        MMQuest.register();

    }
    @Override
    public void onDisable() {
        CommandAPI.onDisable();

    }
}