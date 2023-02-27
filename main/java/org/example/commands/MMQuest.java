package org.example.commands;
import com.google.gson.Gson;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.example.AllMyQuests;
import org.example.Main;
import org.example.Quest;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                        Component mmQuestOutput = Component.text("").color(TextColor.color(0xB2BEB5));
                        mmQuestOutput = mmQuestOutput.append(blockOne);
                        if (quest.checkQuestCompletion(target)) {mmQuestOutput=mmQuestOutput.append(blockOnep2);} //adds "Complete" if quest is complete.
                        else {mmQuestOutput=mmQuestOutput.append(blockOnep3);} //otherwise adds "Incomplete".
                        mmQuestOutput = mmQuestOutput.append(blockTwo);
                        mmQuestOutput = mmQuestOutput.append(blockThree);
                        mmQuestOutput = mmQuestOutput.append(blockFour);

                        player.sendMessage(mmQuestOutput);

                       //Play cool sound :)
                        Location location = player.getLocation();
                        Sound sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
                        float volume = 1.0f;
                        float pitch = 1.0f;
                        player.playSound(location, sound, volume, pitch);
                    })

                .register();

        // Create Command #2: This is /mmquest <target> <quest_name> <value>.
        //              NOTE: This command sets the score of the target to that value, then brings up the progress view in chat.

        // Adds my new <value> argument to the others:
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
                                // VALUE ARGUMENT CODE:
                    // Check if "value" is not a number, then turn it into an int.
                    String valueArgument = (String) args[2];

                    try {
                        int valueArgumentInt = Integer.parseInt(valueArgument);
                    } catch (NumberFormatException ex) {
                        throw CommandAPI.failWithString("Invalid value!");
                    }

                    int valueArgumentInt = Integer.parseInt(valueArgument);

                    // Set the player score to that value.
                    //  NOTE: this code is similar to our getPlayerScore() method of the Quest class.
                    Scoreboard scoreboard = target.getScoreboard();
                    if (scoreboard==null) {throw CommandAPI.failWithString("Player is glitched!");}
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
                    Component mmQuestOutput = Component.text("").color(TextColor.color(0xB2BEB5));
                    mmQuestOutput = mmQuestOutput.append(blockOne);
                    if (quest.checkQuestCompletion(target)) {mmQuestOutput=mmQuestOutput.append(blockOnep2);} //adds "Complete" if quest is complete.
                    else {mmQuestOutput=mmQuestOutput.append(blockOnep3);} //otherwise adds "Incomplete".
                    mmQuestOutput = mmQuestOutput.append(blockTwo);
                    mmQuestOutput = mmQuestOutput.append(blockThree);
                    mmQuestOutput = mmQuestOutput.append(blockFour);

                    player.sendMessage(mmQuestOutput);

                    //Play cool sound :)
                    Location location = player.getLocation();
                    Sound sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
                    float volume = 1.0f;
                    float pitch = 1.0f;
                    player.playSound(location, sound, volume, pitch);
                })

                .register();

    }


    public static void setupQuests() throws IOException {
        // Grabs file path for JSON file, parses it, places the results in an instance of AllMyQuests:
        Gson gson = new Gson();
        File file = new File(Main.getINSTANCE().getDataFolder().getAbsolutePath() + "/mmquest.json" );
        AllMyQuests allMyQuests = gson.fromJson(new FileReader(file),AllMyQuests.class);

        // NOTE: The allMyQuests object is used to store the JSON file's Quest objects in an arraylist.

        // Sets up Hashmap which pairs String (questName) with its corresponding Quest instance:
        stringQuestHashMap = new HashMap<String, Quest>();
        for (Quest quest : allMyQuests.questArrayList) {
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
            if (questInstance==null) {
                // If the requirement is not actually a valid quest (such as "Completion of White Wool Dungeon"),
                //  simply list the requirement, then continue the loop.
                Component requiredQuest = Component.text("  " + quest.questReqs[i])
                        .hoverEvent(Component.text("Check manually!"))
                        .color(TextColor.color(0xFFFFFF));
                blockTwo = blockTwo.append(requiredQuest);
                continue;
            }
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

        // Workflow:
        // 1. For each entry in our questValues HashMap:
        // 2. Create a textcomponent with the key (as an Integer) as the text, value (String) as the hoverevent.

        for (Map.Entry<String, String> valuePair : quest.questValues.entrySet()) {
            String questValueScore = valuePair.getKey();
            String questValueDesc = valuePair.getValue();

            // Workflow: Convert questValueScore into a nice int, to use in clickEvent:
            // Case 1: "5" -> Integer.parseInt();
            // Case 2: "5 - does good stuff" -> split after the number, then Integer.parseInt();
            // Case 3: "has a special tag" -> skip;
            int questValueScoreInt = -100;
            if (questValueScore.matches("[0-9]+")) { // Only contains digits
                // Case 1:
                questValueScoreInt = Integer.parseInt(questValueScore);
            }
            else {
                String[] parts = questValueScore.split("(?<=\\d)(?=\\D+)", 2); // split after the number
                String firstPart=parts[0].trim();
                if (firstPart.matches("[0-9]+")) {
                    // Case 2:
                    questValueScoreInt = Integer.parseInt(firstPart);
                }
                    // Case 3:
                    // Do nothing.
            }

            // Place each score in brackets with its description as a hoverevent:
            blockFour = blockFour.append(Component.text(" ["));

            Component questValue = Component.text(" "+questValueScore+" ")
                    .hoverEvent(Component.text(questValueDesc))
                    //clickEvent: /mmquest <target> <quest_name (remove whitespace)> <value>.
                    .clickEvent(ClickEvent.runCommand("/mmquest "+player.getName()+" "+quest.questName.replaceAll("\\s", "")+" "+questValueScoreInt))
                    .decoration(TextDecoration.BOLD, quest.getPlayerScore(player) == questValueScoreInt)
                    .decoration(TextDecoration.UNDERLINED, quest.getPlayerScore(player) == questValueScoreInt);
            blockFour = blockFour.append(questValue);

            blockFour = blockFour.append(Component.text("] "));
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
