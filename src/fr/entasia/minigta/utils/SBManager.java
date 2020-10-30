package fr.entasia.minigta.utils;

import fr.entasia.apis.other.ScoreBoardHelper;
import fr.entasia.minigta.Main;
import org.bukkit.entity.Player;

public class SBManager extends ScoreBoardHelper {

	public SBManager(Player p){
		super(p, "minigta", "§cMini§6GTA");
	}

	@Override
	protected void setSlots() {

	}

	// -----
	// wait mode
	// -----

	public void setWaitMode(){
		updateWaitPlayers();
		dynamicLine(1, " ");
	}

	public void updateWaitPlayers(){
		staticLine(2, "§7Joueurs connectés : "+Main.instance.pList.size());
	}

	// -----
	// wait mode
	// -----

	public void setGameMode(){
		clear();

		staticLine(5, "§4");
		staticLine(3, "§3");
		staticLine(1, "§2");
		refreshScore();

		refreshTime("5:00");
	}

	public void refreshTime(String time){
		dynamicLine(0, "§7Temps restant :"+time);
	}

	public void refreshScore(){
		dynamicLine(4, "§9Bleus : "+Main.instance.BluePoint);
		dynamicLine(2, "§cRouges : "+Main.instance.RedPoint);
	}

}