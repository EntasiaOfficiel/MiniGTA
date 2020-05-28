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
			if(Main.instance.BlueTeam.contains(p.getName())){
				p.teleport(Main.instance.BlueSpawn);
			}
			else {
				p.teleport(Main.instance.RedSpawn);
			}
			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			cancel();




		}
	}

}
