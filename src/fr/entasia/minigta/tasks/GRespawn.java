package fr.entasia.minigta.tasks;

import fr.entasia.minigta.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GRespawn extends BukkitRunnable{
	
	private int timer = 3;
	private Player p;

	public GRespawn(Player p) {
		this.p = p;
	}

	@Override
	public void run() {
		
		timer--;

		if(timer==0) {
			Main.respawn(p);

			cancel();




		}
	}

}
