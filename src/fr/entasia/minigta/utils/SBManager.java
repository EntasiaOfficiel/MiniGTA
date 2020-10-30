package fr.entasia.minigta.utils;

import fr.entasia.minigta.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class SBManager {

	public Player player;
	public Scoreboard scoreboard;
	public Objective objective;

	public SBManager(Player p){
		if(Main.instance.boards.containsKey(p)) return;

		this.player = p;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		String id = "sb." + Main.r.nextInt(999999);
		this.objective = scoreboard.registerNewObjective(id, "dummy", "§cMini§6GTA");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		Main.instance.boards.put(p, this);

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
		for(String ligne : scoreboard.getEntries()) {
			if (ligne.contains("§7Joueurs connectés")) {
				scoreboard.resetScores(ligne);
			}
		}
		objective.getScore("§7Joueurs connectés : "+ Main.instance.pList.size()).setScore(1);
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

	public void set() {
		player.setScoreboard(scoreboard);
	}

}