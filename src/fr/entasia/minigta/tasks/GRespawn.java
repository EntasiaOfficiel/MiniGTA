package fr.entasia.minigta.tasks;

import fr.entasia.minigta.Main;
import fr.entasia.minigta.utils.GPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
