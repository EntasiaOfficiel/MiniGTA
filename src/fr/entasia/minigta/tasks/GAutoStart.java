package fr.entasia.minigta.tasks;

import fr.entasia.minigta.utils.GPlayer;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import static fr.entasia.minigta.Main.instance;

public class GAutoStart extends BukkitRunnable{
	
	private int timer = 60;
	
	@Override
	public void run() {
		if(timer==10||timer==5||timer==3||timer==2||timer==1) instance.waitspawn.getWorld().playSound(instance.waitspawn, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.0F, 0.533F);

		else if(timer==0){
			instance.startGame();
			cancel();
		}
		for(GPlayer gp : instance.pList.values()) {
			gp.p.setLevel(timer);
		}
		timer--;
	}
}
