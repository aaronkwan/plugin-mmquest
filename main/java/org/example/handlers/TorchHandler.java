package org.example.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Torch;
import org.bukkit.scoreboard.ScoreboardManager;
import org.example.Main;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class TorchHandler implements Listener {
    public TorchHandler(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onTorchPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.TORCH) return;
        if (!event.getPlayer().hasPermission("mmquest.canuse")) return;
        Bukkit.getLogger().info("A torch was placed!");
        Player player = event.getPlayer();

        ArrayList<String> questReqs = new ArrayList<>();
        questReqs.add("A Crown of Topaz");
        questReqs.add("A Crown of Zyreon");

        ArrayList<String> questValues = new ArrayList<>();
        questValues.add("X");
        questValues.add("X");
        questValues.add("X");
        questValues.add("X");
        questValues.add("X");
        questValues.add("X");
        questValues.add("Unstarted");
        questValues.add("Speaking to Aimee to receive quest");
        questValues.add("Speaking to Aimee to receive quest");
        questValues.add("Speaking to Aimee to receive quest");
        questValues.add("Speaking to Aimee to receive quest");
        questValues.add("Speaking to Aimee to receive quest");
        questValues.add("Returned jewels / speak to Aimee");
        questValues.add("Complete");


        final String questName = "A Crown of Majesty";
        final String questNumber = "Quest01";
        final String questDescription = "Quest Start: Aimee (-761 106 22)";

        Component myMessage = Component.text("------------------------------------------")
                .append(Component.newline())
                .color(TextColor.color(0xB2BEB5))
        .append(Component.text(questName + " (" + questNumber + ")" + " for " + player.getName() + ":"))
                .append(Component.newline())
                .append(Component.text("Requirements:"));

        for (int i = 0; i < questReqs.size(); i++) {
            Component newItem = Component.text("  " + questReqs.get(i) + "  ")
                   // add a .hoverEvent unstarted/in prog/completed.
                    .hoverEvent(Component.text("Complete"))
                    .clickEvent(ClickEvent.runCommand("going to " + questReqs.get(i) + "!"))
                    .color(TextColor.color(0x00ff00));  //should change based on getcompletion()
            myMessage = myMessage.append(newItem);
        }


        myMessage = myMessage.append(Component.newline());
        myMessage = myMessage.append(Component.text(questDescription));
        myMessage = myMessage.append(Component.newline());
        myMessage = myMessage.append(Component.text("Values: "));
        myMessage = myMessage.append(Component.newline());


        for (int i = 0; i < questValues.size(); i++) {
            Component newItem = Component.text(" [" + i + "] ")
                    .hoverEvent(Component.text(questValues.get(i)))
                    .clickEvent(ClickEvent.suggestCommand("/scoreboard players set " + player.getName() + " " + questNumber + " " + i))
                    .decoration(TextDecoration.BOLD, getPlayerScore(player, "Quest01") == i)
                    .decoration(TextDecoration.UNDERLINED, getPlayerScore(player, "Quest01") == i);
            myMessage = myMessage.append(newItem);
        }
        myMessage = myMessage.append(Component.newline());
        myMessage = myMessage.append(Component.text("------------------------------------------"));
        player.sendMessage(myMessage);


    }
    public int getPlayerScore(Player player, String objectiveName) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective==null) {return -1;}
        return objective.getScore(player.getName()).getScore();
    }
}

