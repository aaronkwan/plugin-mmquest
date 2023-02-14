package org.example;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
//import java.util.ArrayList;

public class Quest {
    public String questName = "";
    public String questNumber = "";
    public String questDescription = "";
    public Integer questCompleteScore = 0; //used if a quest has a normal completion score; >=.
    public Integer questCompleteScore2 = -2; //used if a quest has two completion scores (Quest144); ==.

    //public ArrayList<String> questReqs = new ArrayList<>();
    //public ArrayList<String> questValues = new ArrayList<>();
    public String[] questReqs;
    public String[] questValues;

    public boolean checkQuestCompletion(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(questNumber);
        if (objective==null) {return false;}

        Integer playerScore = objective.getScore(player.getName()).getScore();
        if (playerScore >= questCompleteScore || playerScore == questCompleteScore2) {
            return true;
        }
        else {
            return false;
        }

    }

    public int getPlayerScore(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard==null) {return -1;}
        Objective objective = scoreboard.getObjective(questNumber);
        if (objective==null) {return -1;}
        return objective.getScore(player.getName()).getScore();
    }

    //Constructor:
    public Quest(String a, String b, String c,
                 Integer d, Integer e, String[] f, String[] g) {
        questName = a;
        questNumber = b;
        questDescription = c;
        questCompleteScore = d;
        questCompleteScore2 = e;
        questReqs = f;
        questValues = g;
    }
}
