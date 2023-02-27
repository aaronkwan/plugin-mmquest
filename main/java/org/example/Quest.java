package org.example;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Quest {
    public String questName = "";
    public String questNumber = "";
    public String questDescription = "";
    public Integer[] questCompleteScores;
    public String[] questReqs;
    public LinkedHashMap<String,String> questValues;

    public boolean checkQuestCompletion(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(questNumber);
        if (objective==null) {return false;}

        Integer playerScore = objective.getScore(player.getName()).getScore();
        boolean matchesAny = false;
        // If empty array, just return false:
        if (questCompleteScores.length == 0) {
            return false;
        }
        // Keep track of maximum:
        int max = Integer.MIN_VALUE;
        for (Integer score : questCompleteScores) {
            if (score==playerScore) {
                matchesAny = true;
                break;
            }
            if (score>max) {
                max=score;
            }
        }
        // Test if player's score is higher than the max:
        if (playerScore>max) {
            return true;
        }
        return (matchesAny);
    }

    public int getPlayerScore(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard==null) {return -1;}
        Objective objective = scoreboard.getObjective(questNumber);
        if (objective==null) {return -1;}
        return objective.getScore(player.getName()).getScore();
    }


    //Constructor:
    public Quest(String questName, String questNumber, String questDescription,
                 Integer[] questCompleteScores, String[] questReqs, LinkedHashMap<String, String> questValues) {
        this.questName = questName;
        this.questNumber = questNumber;
        this.questDescription = questDescription;
        this.questCompleteScores = questCompleteScores;
        this.questReqs = questReqs;
        this.questValues = questValues;
    }
}
