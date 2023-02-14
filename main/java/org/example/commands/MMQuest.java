package org.example.commands;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.example.Quest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MMQuest {
    private static HashMap<String, Quest> stringQuestHashMap;  //declaring my hashmap, see below.
    public static void register() {

        //turning hashmap keySet into a list to use in argument suggestion
        List<String> questSuggestions = new ArrayList<>(stringQuestHashMap.keySet());

        //command arguments
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(
                new EntitySelectorArgument.OnePlayer("target")
        );
        arguments.add(
                new StringArgument("quest_name").replaceSuggestions(ArgumentSuggestions.strings(questSuggestions))
        );

        // Create the command:
        new CommandAPICommand("mmquest")
                .withArguments(
                        //new EntitySelectorArgument.OnePlayer("target"),
                        //new MultiLiteralArgument("r1", "r2", "r3", "all"),
                        arguments
                )
                .withAliases("mmq")       // Command aliases
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    Player target = (Player) args[0];
                    String questArgument = (String) args[1];
                    Quest quest = getQuestObj(questArgument);
                    if (quest==null) {
                        player.sendMessage(Component.text("The alias for this quest does not exist!").color(TextColor.color(0xFF5555)));
                    }
                    else {
                        //Message Components:
                        //Block 1: first spacer, <Quest Name> (<Quest Number>) for <Player>: <complete>
                        //Block 2: Requirements: <required quests>
                        //Block 3: <Quest Description>; Values:
                        //Block 4: <Quest Values>. Score:

                        //Block #1:
                        Component blockOne =
                                Component.text("")
                                        .append(Component.text("------------------------------------------"))
                                        .append(Component.newline())
                                        .append(Component.text(quest.questName + " (" + quest.questNumber + ")" + " for " + target.getName() + ":"));
                        Component blockOnep2 =
                                Component.text(" Complete").color(TextColor.color(0x00ff00));
                        //Block #2:
                        Component blockTwo = getRequiredQuests(target, quest);
                        //Block #3:
                        Component blockThree = Component.text("")
                                .append(Component.newline())
                                .append(Component.text(quest.questDescription))
                                .append(Component.newline())
                                .append(Component.text("Values: "))
                                .append(Component.newline());
                        //Block #4:
                        Component blockFour = getQuestValues(target, quest);

                        //Combine Components:
                        Component mmQuestOutput = Component.text("").color(TextColor.color(0xB2BEB5));;
                        mmQuestOutput = mmQuestOutput.append(blockOne);
                        if (quest.checkQuestCompletion(player)) {mmQuestOutput=mmQuestOutput.append(blockOnep2);} //adds a <complete> if quest is complete.
                        mmQuestOutput = mmQuestOutput.append(blockTwo);
                        mmQuestOutput = mmQuestOutput.append(blockThree);
                        mmQuestOutput = mmQuestOutput.append(blockFour);


                        player.sendMessage(mmQuestOutput);
                    }})

                .register();
    }


    public static void setupQuests() {
        //creates & sets up all the Quest objects inside an arraylist:
        ArrayList<Quest> questArrayList = new ArrayList<Quest>();
        questArrayList.add(new Quest("A Crown of Topaz", "Quest01", "Quest Start: Aimee (-761 106 22)",
                6, 6, new String[] {}, new String[] {"Unstarted","Speaking to Aimee for the first time","Speaking to Aimee for the first time",
                "Speaking to Aimee for the first time","Speaking to Aimee for the first time","Returned the Topaz / Speak to Aimee","Quest Complete"}));
        questArrayList.add(new Quest("A Crown of Majesty", "Quest01", "Quest Start: Aimee (-761 106 22)",
                13, 13, new String[] {"A Crown of Topaz"}, new String[] {"X","X","X","X","X","X","Unstarted","Speaking to Aimee to receive quest","Speaking to Aimee to receive quest",
                "Speaking to Aimee to receive quest","Speaking to Aimee to receive quest","Speaking to Aimee to receive quest",
                "Returned jewels / speak to Aimee","Quest Complete"}));
        questArrayList.add(new Quest("Bandit Troubles", "Quest02", "Quest Start: Octavius (-673 108 71)",
                12, 8, new String[] {}, new String[] {"Unstarted","Speaking to Octavius to receive quest","Speaking to Octavius to receive quest","Speaking to Octavius to receive quest",
        "Speaking to Octavius to receive quest","Returned caravan loot to Octavius","X","X","Quest Complete (Bad Path)","Chose to report to Murano","Speaking to Murano",
        "Speaking to Murano","Quest Complete (Good Path)"}));
        questArrayList.add(new Quest("Mages Legacy", "Quest03", "Quest Start: Vargos (-735 155 116)",
                21, 21, new String[] {}, new String[] {"Unstarted","Speaking to Vargos","Speaking to Vargos","Speaking to Vargos","Speaking to Vargos",
                "Speaking to Vargos","Speaking to Vargos","Speaking to Vargos","Speaking to Vargos","Tasked to find Ezariah's notes",
        "Returned notes to Vargos","Tasked to go to the office on the roof","X","Tasked to find Hermy (-320, 92, 340)","Got gloop from witch’s village (-390, 95, 400) and gave them to Hermy",
        "Received translated notes from Hermy","Quest Complete"}));
        /*        Sample New Quest:
        questArrayList.add(new Quest("Bandit Troubles", "Quest02", "Quest Start: Octavius (-673 108 71)",
                       12, 8, new String[] {}, new String[] {"Unstarted"}));
        */


        //sets up Hashmap which pairs String (questName) with its corresponding Quest instance:
        stringQuestHashMap = new HashMap<String, Quest>();
        for (Quest quest : questArrayList) {
            String questString = quest.questName.replaceAll("\\s", "");
            stringQuestHashMap.put(questString,quest);
        }
    }
    public static Quest getQuestObj(String questArgument) {
        //accepts a String; outputs the corresponding Quest obj/instance.
        String questString = questArgument.replaceAll("\\s", ""); //remove whitespace
        return stringQuestHashMap.get(questString);
    }

    public static Component getRequiredQuests(Player player, Quest quest) {

        Component blockTwo = Component.text("")
                .append(Component.newline())
                .append(Component.text("Requirements:"));
        for (int i = 0; i < quest.questReqs.length; i++) {

            // For each Quest name listed inside our quest's questReqs[], we:
            // 1: remove whitespace.
            // 2: use getQuestObj() to return our Quest instance.
            // 3: use checkQuestCompletion() on our Quest instance.
            String questString = quest.questReqs[i].replaceAll("\\s","");
            Quest questInstance = getQuestObj(questString);
            boolean isQuestComplete = questInstance.checkQuestCompletion(player);

            if (isQuestComplete) {
                Component requiredQuest = Component.text("  " + quest.questReqs[i] + "  ")
                        .hoverEvent(Component.text("Complete"))
                        .clickEvent(ClickEvent.runCommand("/mmq " + player.getName() + " " + questString))
                        .color(TextColor.color(0x00ff00));
                blockTwo = blockTwo.append(requiredQuest);
            }
            else    {
                Component requiredQuest = Component.text("  " + quest.questReqs[i] + "  ")
                        .hoverEvent(Component.text("Uncomplete"))
                        .clickEvent(ClickEvent.runCommand("/mmq " + player.getName() + " " + questString))
                        .color(TextColor.color(0xFF5555));
                blockTwo = blockTwo.append(requiredQuest);
            }
        }

        if (quest.questReqs.length==0) {blockTwo = blockTwo.append(Component.text(" None"));}
        return blockTwo;
    }
    public static Component getQuestValues(Player player, Quest quest) {

        Component blockFour = Component.text("");

        for (int i = 0; i < quest.questValues.length; i++) {
            Component questValue = Component.text(" [" + i + "] ")
                    .hoverEvent(Component.text(quest.questValues[i]))
                    .clickEvent(ClickEvent.suggestCommand("/scoreboard players set " + player.getName() + " " + quest.questNumber + " " + i))
                    .decoration(TextDecoration.BOLD, quest.getPlayerScore(player) == i)
                    .decoration(TextDecoration.UNDERLINED, quest.getPlayerScore(player) == i);
            blockFour = blockFour.append(questValue);
        }

        if (quest.getPlayerScore(player)==-1) {
            blockFour = blockFour.append(Component.text("   [Score unset or Scoreboard nonexistent]").decoration(TextDecoration.ITALIC,true));
        }
        else {
            blockFour = blockFour.append(Component.text("   [Score: " + quest.getPlayerScore(player) + "]").decoration(TextDecoration.ITALIC,true));
        }
        blockFour = blockFour.append(Component.newline());
        blockFour = blockFour.append(Component.text("------------------------------------------"));
        return blockFour;
    }

    }
