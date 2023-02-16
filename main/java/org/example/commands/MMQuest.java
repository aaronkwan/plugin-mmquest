package org.example.commands;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.example.Quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MMQuest {
    private static HashMap<String, Quest> stringQuestHashMap;  //declaring my hashmap, see function setupQuests().
    public static void register() {

        //turning hashmap keySet into a list to use in argument suggestion
        List<String> questSuggestions = new ArrayList<>(stringQuestHashMap.keySet());

        //adding my arguments for <target> and <quest_name> (with the above suggestions)
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(
                new EntitySelectorArgument.OnePlayer("target")
        );
        arguments.add(
                new StringArgument("quest_name").replaceSuggestions(ArgumentSuggestions.strings(questSuggestions))
        );

        // Create Command #1: This is /mmquest <target> <quest_name>:
        //  NOTE: This command brings up the progress view of a quest for a specific player in chat.
        new CommandAPICommand("mmquest")
                .withArguments(arguments)
                .withAliases("mmq")       // Command aliases
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    Player target = (Player) args[0];
                    String questArgument = (String) args[1];
                    Quest quest = getQuestInstance(questArgument);
                    // NOTE: 'player' is the player who ran the command, while 'target' is the player to grab quest scores from.
                    //          we want to run functions on 'target', but send the final message component to 'player'.

                    if (quest==null) {
                        throw CommandAPI.failWithString("The alias for this quest does not exist!");
                    }

                        //Message Components:
                        //blockOne: first spacer, <Quest Name> (<Quest Number>) for <player>:
                        //blockOnep2: "Complete" if the quest is complete.
                        //blockTwo: Requirements: <required quests>
                        //blockThree: <Quest Description>; Values:
                        //blockFour: <Quest Values>. Score: X

                        Component blockOne =
                                Component.text("")
                                        .append(Component.text("------------------------------------------"))
                                        .append(Component.newline())
                                        .append(Component.text(quest.questName + " (" + quest.questNumber + ")" + " for " + target.getName() + ":"));

                        Component blockOnep2 =
                                Component.text(" Complete").color(TextColor.color(0x00ff00));

                    Component blockOnep3 =
                            Component.text(" Incomplete").color(TextColor.color(0xFF5555));

                        Component blockTwo = getRequiredQuests(target, quest);

                        Component blockThree = Component.text("")
                                .append(Component.newline())
                                .append(Component.text(quest.questDescription))
                                .append(Component.newline())
                                .append(Component.text("Values: "))
                                .append(Component.newline());

                        Component blockFour = getQuestValues(target, quest);

                        //Combine my Component blocks:
                        Component mmQuestOutput = Component.text("").color(TextColor.color(0xB2BEB5));;
                        mmQuestOutput = mmQuestOutput.append(blockOne);
                        if (quest.checkQuestCompletion(target)) {mmQuestOutput=mmQuestOutput.append(blockOnep2);} //adds "Complete" if quest is complete.
                        else {mmQuestOutput=mmQuestOutput.append(blockOnep3);} //otherwise adds "Incomplete".
                        mmQuestOutput = mmQuestOutput.append(blockTwo);
                        mmQuestOutput = mmQuestOutput.append(blockThree);
                        mmQuestOutput = mmQuestOutput.append(blockFour);

                        player.sendMessage(mmQuestOutput);
                    })

                .register();

        // Create Command #2: This is /mmquest <target> <quest_name> <value>.
        //              NOTE: This command sets the score of the target to that value, then brings up the progress view in chat.

        arguments.add(new StringArgument("value"));

        new CommandAPICommand("mmquest")
                .withArguments(arguments)
                .withAliases("mmq")       // Command aliases
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    Player target = (Player) args[0];
                    String questArgument = (String) args[1];
                    Quest quest = getQuestInstance(questArgument);
                    if (quest==null) {
                        throw CommandAPI.failWithString("The alias for this quest does not exist!");
                    }

                    // Check if "value" is not a number, then turn it into an int.
                    String valueArgument = (String) args[2];
                    if (valueArgument.matches("\\D")) {
                        throw CommandAPI.failWithString("Invalid value!"); //Note: entering a non-digit argument does not send this message for some reason.
                    }
                    int valueArgumentInt = Integer.parseInt(valueArgument);

                    // Set the player score to that value.
                    //  NOTE: this code is similar to our getPlayerScore() method of the Quest class.
                    Scoreboard scoreboard = target.getScoreboard();
                    if (scoreboard==null) {throw CommandAPI.failWithString("Player is glitched!");} //Note: I have not been able to get this error.
                    Objective objective = scoreboard.getObjective(quest.questNumber);
                    if (objective==null) {throw CommandAPI.failWithString("Scoreboard objective does not exist!");}
                    objective.getScore(target.getName()).setScore(valueArgumentInt);

                    // Send the custom progress view:
                    Component blockOne =
                            Component.text("")
                                    .append(Component.text("------------------------------------------"))
                                    .append(Component.newline())
                                    .append(Component.text(quest.questName + " (" + quest.questNumber + ")" + " for " + target.getName() + ":"));

                    Component blockOnep2 =
                            Component.text(" Complete").color(TextColor.color(0x00ff00));

                    Component blockOnep3 =
                            Component.text(" Incomplete").color(TextColor.color(0xFF5555));

                    Component blockTwo = getRequiredQuests(target, quest);

                    Component blockThree = Component.text("")
                            .append(Component.newline())
                            .append(Component.text(quest.questDescription))
                            .append(Component.newline())
                            .append(Component.text("Values: "))
                            .append(Component.newline());

                    Component blockFour = getQuestValues(target, quest);

                    //Combine my Component blocks:
                    Component mmQuestOutput = Component.text("").color(TextColor.color(0xB2BEB5));;
                    mmQuestOutput = mmQuestOutput.append(blockOne);
                    if (quest.checkQuestCompletion(target)) {mmQuestOutput=mmQuestOutput.append(blockOnep2);} //adds "Complete" if quest is complete.
                    else {mmQuestOutput=mmQuestOutput.append(blockOnep3);} //otherwise adds "Incomplete".
                    mmQuestOutput = mmQuestOutput.append(blockTwo);
                    mmQuestOutput = mmQuestOutput.append(blockThree);
                    mmQuestOutput = mmQuestOutput.append(blockFour);

                    player.sendMessage(mmQuestOutput);
                })

                .register();
    }


    public static void setupQuests() {
        //creates & sets up all the Quest instances inside an arraylist:
        ArrayList<Quest> questArrayList = new ArrayList<Quest>();
        questArrayList.add(new Quest("A Crown of Topaz", "Quest01", "Quest Start: Aimee (-761 106 22)",
                new Integer[]{6,7,8,9,10,11,12,13}, new String[] {}, new String[] {"Unstarted","Speaking to Aimee for the first time","Speaking to Aimee for the first time",
                "Speaking to Aimee for the first time","Speaking to Aimee for the first time","Returned the Topaz / Speak to Aimee","Quest Complete"}));
        questArrayList.add(new Quest("A Crown of Majesty", "Quest01", "Quest Start: Aimee (-761 106 22)",
                new Integer[]{13}, new String[] {"A Crown of Topaz"}, new String[] {"X","X","X","X","X","X","Unstarted","Speaking to Aimee to receive quest","Speaking to Aimee to receive quest",
                "Speaking to Aimee to receive quest","Speaking to Aimee to receive quest","Speaking to Aimee to receive quest",
                "Returned jewels / speak to Aimee","Quest Complete"}));
        questArrayList.add(new Quest("Bandit Troubles", "Quest02", "Quest Start: Octavius (-673 108 71)",
                new Integer[]{8,12}, new String[] {}, new String[] {"Unstarted","Speaking to Octavius to receive quest","Speaking to Octavius to receive quest","Speaking to Octavius to receive quest",
        "Speaking to Octavius to receive quest","Returned caravan loot to Octavius","X","X","Quest Complete (Bad Path)","Chose to report to Murano","Speaking to Murano",
        "Speaking to Murano","Quest Complete (Good Path)"}));
        questArrayList.add(new Quest("Mages Legacy", "Quest03", "Quest Start: Vargos (-735 155 116)",
                new Integer[]{21}, new String[] {}, new String[] {"Unstarted","Speaking to Vargos","Speaking to Vargos","Speaking to Vargos","Speaking to Vargos",
                "Speaking to Vargos","Speaking to Vargos","Speaking to Vargos","Speaking to Vargos","Tasked to find Ezariah's notes",
        "Returned notes to Vargos","Tasked to go to the office on the roof","X","Tasked to find Hermy (-320, 92, 340)","Got gloop from witch's village (-390, 95, 400) and gave them to Hermy",
        "Received translated notes from Hermy","X","X","X","X","X","Quest Complete"}));
        /*        Template for adding new Quests:
        questArrayList.add(new Quest("Bandit Troubles", "Quest02", "Quest Start: Octavius (-673 108 71)",
                       new Integer[]{8,12}, new String[] {"A Required Quest"}, new String[] {"Unstarted","In Progress", "Finished"}));
        */


        //sets up Hashmap which pairs String (questName) with its corresponding Quest instance:
        stringQuestHashMap = new HashMap<String, Quest>();
        for (Quest quest : questArrayList) {
            String questString = quest.questName.replaceAll("\\s", "");
            stringQuestHashMap.put(questString,quest);
        }
    }
    public static Quest getQuestInstance(String questArgument) {
        //accepts a String; outputs the corresponding Quest instance.
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
            Quest questInstance = getQuestInstance(questString);
            boolean isQuestComplete = questInstance.checkQuestCompletion(player);
            // If the quest is complete, show its name with color green. Else, color red.
            if (isQuestComplete) {
                Component requiredQuest = Component.text("  " + quest.questReqs[i] + "  ")
                        .hoverEvent(Component.text("Complete"))
                        .clickEvent(ClickEvent.runCommand("/mmquest " + player.getName() + " " + questString))
                        .color(TextColor.color(0x00ff00));
                blockTwo = blockTwo.append(requiredQuest);
            }
            else    {
                Component requiredQuest = Component.text("  " + quest.questReqs[i] + "  ")
                        .hoverEvent(Component.text("Incomplete"))
                        .clickEvent(ClickEvent.runCommand("/mmquest " + player.getName() + " " + questString))
                        .color(TextColor.color(0xFF5555));
                blockTwo = blockTwo.append(requiredQuest);
            }
        }
            //If our quest's questReqs[] is empty, append a "None".
        if (quest.questReqs.length==0) {blockTwo = blockTwo.append(Component.text(" None"));}
        return blockTwo;
    }
    public static Component getQuestValues(Player player, Quest quest) {

        Component blockFour = Component.text("");

        for (int i = 0; i < quest.questValues.length; i++) {

            //if that value is a placeholder (aka is "X"), skip it:
            if (quest.questValues[i]=="X") {continue;}
            //otherwise, place the value in brackets with hoverevent its description:
            Component questValue = Component.text(" [" + i + "] ")
                    .hoverEvent(Component.text(quest.questValues[i]))
                    //clickEvent: /mmquest <target> <quest_name (remove whitespace)> <value>.
                    .clickEvent(ClickEvent.runCommand("/mmquest "+player.getName()+" "+quest.questName.replaceAll("\\s", "")+" "+i))
                    .decoration(TextDecoration.BOLD, quest.getPlayerScore(player) == i)
                    .decoration(TextDecoration.UNDERLINED, quest.getPlayerScore(player) == i);
            blockFour = blockFour.append(questValue);
        }

        if (quest.getPlayerScore(player)==-1) {
            blockFour = blockFour.append(Component.text("   [Scoreboard does not exist]").decoration(TextDecoration.ITALIC,true));
        }
        else {
            blockFour = blockFour.append(Component.text("   [Score: " + quest.getPlayerScore(player) + "]").decoration(TextDecoration.ITALIC,true));
        }
        blockFour = blockFour.append(Component.newline());
        blockFour = blockFour.append(Component.text("------------------------------------------"));
        return blockFour;
    }

    }
