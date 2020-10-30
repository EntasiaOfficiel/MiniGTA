package fr.entasia.minigta.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RefreshRunnable extends BukkitRunnable {

	private Player target;
	private Player me;

	public RefreshRunnable(Player me, Player target){
		this.me = me;
		this.target = target;
	}

	@Override
	public void run() {
		me.setCompassTarget(target.getLocation());
	}
}
