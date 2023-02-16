package org.example.commands;
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
                    Component mmQuestOutput = Component.text("").color(TextColor.color(0xB2BEB5));;
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


    public static void setupQuests() {
        //creates & sets up all the Quest instances inside an arraylist:
        ArrayList<Quest> questArrayList = new ArrayList<Quest>();
        questArrayList.add(new Quest("A Crown of Topaz", "Quest01", "Quest Start: Aimee (-761 106 22)",
                new Integer[]{6,7,8,9,10,11,12,13}, new String[] {}, new String[] {"0 Unstarted","1 Speaking to Aimee for the first time","2 Speaking to Aimee for the first time",
                "3 Speaking to Aimee for the first time","4 Speaking to Aimee for the first time","5 Returned the Topaz / Speak to Aimee","6 Quest Complete"}));
        questArrayList.add(new Quest("A Crown of Majesty", "Quest01", "Quest Start: Aimee (-761 106 22)",
                new Integer[]{13}, new String[] {"A Crown of Topaz"}, new String[] {"6 Unstarted","7 Speaking to Aimee to receive quest","8 Speaking to Aimee to receive quest",
                "9 Speaking to Aimee to receive quest","10 Speaking to Aimee to receive quest","11 Speaking to Aimee to receive quest",
                "12 Returned jewels / speak to Aimee","13 Quest Complete"}));
        questArrayList.add(new Quest("Bandit Troubles", "Quest02", "Quest Start: Octavius (-673 108 71)",
                new Integer[]{8,12}, new String[] {}, new String[] {"0 Unstarted","1 Speaking to Octavius to receive quest","2 Speaking to Octavius to receive quest","3 Speaking to Octavius to receive quest",
        "4 Speaking to Octavius to receive quest","5 Returned caravan loot to Octavius","8 Quest Complete (Bad Path)","9 Chose to report to Murano","10 Speaking to Murano",
        "11 Speaking to Murano","12 Quest Complete (Good Path)"}));
        questArrayList.add(new Quest("Mages Legacy", "Quest03", "Quest Start: Vargos (-735 155 116)",
                new Integer[]{21}, new String[] {}, new String[] {"0 Unstarted","1 Speaking to Vargos","2 Speaking to Vargos","3 Speaking to Vargos","4 Speaking to Vargos",
                "5 Speaking to Vargos","6 Speaking to Vargos","7 Speaking to Vargos","8 Speaking to Vargos","9 Tasked to find Ezariah's notes",
        "10 Returned notes to Vargos","11 Tasked to go to the office on the roof","13 Tasked to find Hermy (-320, 92, 340)","14 Got gloop from witch's village (-390, 95, 400) and gave them to Hermy",
        "15 Received translated notes from Hermy","21 Quest Complete"}));
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

        for (int i = 0; i < quest.questValues.length; i++) {

            // Workflow:
            // 1. Take string of the format "X Hello this is a sentence", where X is a number;
            // 2. Separate it into "X" and "Hello this is a sentence";
            // 3. Turn "X" into an integer X (this is a questScore value);
            // 4. "Hello this is a sentence" (this is the description associated with that value);

            String[] questScoreAndScoreDescription = quest.questValues[i].split("\\s",2);
            int questScore = Integer.parseInt(questScoreAndScoreDescription[0]);
            String questScoreDescription = questScoreAndScoreDescription[1];

            // questValue: Places each score in brackets with its description as a hoverevent:
            Component questValue = Component.text(" [" + questScore + "] ")
                    .hoverEvent(Component.text(questScoreDescription))
                    //clickEvent: /mmquest <target> <quest_name (remove whitespace)> <value>.
                    .clickEvent(ClickEvent.runCommand("/mmquest "+player.getName()+" "+quest.questName.replaceAll("\\s", "")+" "+questScore))
                    .decoration(TextDecoration.BOLD, quest.getPlayerScore(player) == questScore)
                    .decoration(TextDecoration.UNDERLINED, quest.getPlayerScore(player) == questScore);
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
