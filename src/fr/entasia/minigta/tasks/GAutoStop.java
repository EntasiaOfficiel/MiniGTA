package fr.entasia.minigta.tasks;

import fr.entasia.apis.other.ChatComponent;
import fr.entasia.minigta.Main;
import fr.entasia.minigta.utils.GPlayer;
import fr.entasia.minigta.utils.SBManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Map;

public class GAutoStop extends BukkitRunnable{

	@Override
	public void run() {

		Main.instance.timer--;
		for(Map.Entry<Player, SBManager> sign : Main.instance.boards.entrySet()){
			String dateformat = new SimpleDateFormat("m:ss").format(Main.instance.timer*1000);
			sign.getValue().refreshTime(dateformat);
		}


		for(GPlayer gp : Main.instance.pList.values()) {
			Location target=null;
			double i = 800;
			double j;
			if(gp.team.equals("blue")){
				for(String lp : Main.instance.RedTeam){
					Location a = Main.instance.pList.get(lp).p.getLocation();
					j = a.distance(gp.p.getLocation());
					if(j<i){
						i=j;
						target=a;
					}
				}
			}else{
				for(String lp : Main.instance.BlueTeam){
					Location a = Main.instance.pList.get(lp).p.getLocation();
					j = a.distance(gp.p.getLocation());
					if(j<i){
						i=j;
						target=a;
					}
				}
			}
			gp.p.setCompassTarget(target);
		}


		if(Main.instance.timer == 120) {
			Main.instance.sendMsg(ChatComponent.create("§7Il ne reste plus que 2 minutes"));
		}else if(Main.instance.timer == 30) {
			for(GPlayer gp : Main.instance.pList.values()) {
				gp.p.sendMessage("§7Il ne reste plus que 30 secondes");
				gp.p.setGlowing(true);
			}
		}else if(Main.instance.timer <= 0) {
			Main.instance.endGame();
			cancel();
		}
	}
}
