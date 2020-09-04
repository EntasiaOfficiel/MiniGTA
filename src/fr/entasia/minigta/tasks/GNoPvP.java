package fr.entasia.minigta.tasks;

import fr.entasia.minigta.Main;
import fr.entasia.minigta.utils.GPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GNoPvP extends BukkitRunnable {

	Player p;
	float timer = 5;

	public GNoPvP(Player p){this.p=p;}

	@Override
	public void run() {
		timer--;
		if(timer==0){
			for(GPlayer gp: Main.instance.pList.values()){
				if(gp.p.getDisplayName().equalsIgnoreCase(p.getDisplayName())){
					gp.noPvp=false;
				}
			}
			cancel();
		}
	}
}
