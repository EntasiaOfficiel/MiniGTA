package fr.entasia.minigta.utils;

import fr.entasia.minigta.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Random;

public class CustomScoreboardManager implements ScoreboardManager{

    public Player player;
    public Scoreboard scoreboard;
    public Objective objective;

    public CustomScoreboardManager(Player p){

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.player = p;

        if(Main.instance.boards.containsKey(p)) return;

        Main.instance.boards.put(p, this);

        String name = "sb." + new Random().nextInt(999999);

        objective = scoreboard.registerNewObjective(name, "dummy");
        objective = scoreboard.getObjective(name);
        objective.setDisplayName("§cMini§6GTA");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    }

    public void refreshTime(String time){
        for(String ligne : scoreboard.getEntries()){

            if(ligne.contains("§7Temps restant")){
                scoreboard.resetScores(ligne);

                objective.getScore(ligne.split(":")[0] + ": "+ time).setScore(0);
            }


        }
    }

    public void refreshScore(){
        for(String ligne : scoreboard.getEntries()){

            if(ligne.contains("§cRouges")){
                scoreboard.resetScores(ligne);

                objective.getScore(ligne.split(":")[0] + ": "+ Main.instance.RedPoint).setScore(2);
            }

            if(ligne.contains("§9Bleus")){
                scoreboard.resetScores(ligne);

                objective.getScore(ligne.split(":")[0] + ": "+ Main.instance.BluePoint).setScore(4);
            }


        }
    }

    public void refreshWaiting(){
        for(String ligne : scoreboard.getEntries()){
            if(ligne.contains("§7Joueurs connectés")){
                scoreboard.resetScores(ligne);

                objective.getScore(ligne.split(":")[0] + " "+ Main.instance.pList.size()).setScore(1);
            }
        }
    }

    public void sendWaitingLine(){
        for(String line : scoreboard.getEntries()){
            scoreboard.resetScores(line);
        }

        objective.getScore("§7Joueurs connectés : "+Main.instance.pList.size()).setScore(1);
        objective.getScore("§e").setScore(0);
    }

    public void sendStartingLine(){
        for(String line : scoreboard.getEntries()){
            scoreboard.resetScores(line);
        }
        objective.getScore( "§7Temps restant : 5:00" ).setScore(0);

        objective.getScore("§2").setScore(1);
        objective.getScore("§cRouges : "+Main.instance.RedPoint).setScore(2);
        objective.getScore("§3").setScore(3);
        objective.getScore("§9Bleus : "+Main.instance.BluePoint).setScore(4);
        objective.getScore("§4").setScore(5);
    }

    @Override
    public Scoreboard getMainScoreboard() {
        return scoreboard;
    }

    @Override
    public Scoreboard getNewScoreboard() {
        return null;
    }

    public void set() {
        player.setScoreboard(getMainScoreboard());
    }

}