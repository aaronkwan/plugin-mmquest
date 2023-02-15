package org.example;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Quest {
    public String questName = "";
    public String questNumber = "";
    public String questDescription = "";
    public int questCompleteScore = 0; //used if a quest has a normal completion score; >=.
    public int questCompleteScore2 = -2; //used if a quest has two completion scores (Quest02); ==.
    public String[] questReqs;
    public String[] questValues;

    public boolean checkQuestCompletion(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(questNumber);
        if (objective==null) {return false;}

        Integer playerScore = objective.getScore(player.getName()).getScore();
        return (playerScore >= questCompleteScore || playerScore == questCompleteScore2);
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
                 int questCompleteScore, int questCompleteScore2, String[] questReqs, String[] questValues) {
        this.questName = questName;
        this.questNumber = questNumber;
        this.questDescription = questDescription;
        this.questCompleteScore = questCompleteScore;
        this.questCompleteScore2 = questCompleteScore2;
        this.questReqs = questReqs;
        this.questValues = questValues;
    }
}
