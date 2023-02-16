package org.example;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Quest {
    public String questName = "";
    public String questNumber = "";
    public String questDescription = "";
    public Integer[] questCompleteScores;
    public String[] questReqs;
    public String[] questValues;

    public boolean checkQuestCompletion(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(questNumber);
        if (objective==null) {return false;}

        Integer playerScore = objective.getScore(player.getName()).getScore();
        boolean matchesAny = false;
        for (Integer score : questCompleteScores) {
            if (score==playerScore) {
                matchesAny = true;
                break;
            }
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
                 Integer[] questCompleteScores, String[] questReqs, String[] questValues) {
        this.questName = questName;
        this.questNumber = questNumber;
        this.questDescription = questDescription;
        this.questCompleteScores = questCompleteScores;
        this.questReqs = questReqs;
        this.questValues = questValues;
    }
}
