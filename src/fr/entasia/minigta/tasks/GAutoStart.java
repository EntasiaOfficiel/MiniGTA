package fr.entasia.minigta.tasks;

import fr.entasia.apis.ChatComponent;
import fr.entasia.minigta.utils.GPlayer;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

import static fr.entasia.minigta.Main.instance;

public class GAutoStart extends BukkitRunnable{
	
	private int timer = 60;
	
	@Override
	public void run() {

		if(timer%20 ==0 && timer!=0){
			instance.sendMsg(ChatComponent.create("§7Vote de la map:"));
			for(Map.Entry<String, FileConfiguration> entry : instance.mapFiles.entrySet()){
				FileConfiguration fileConfiguration = entry.getValue();
				ChatComponent message = new ChatComponent( "§7"+entry.getKey() +" (recommandée de "+ fileConfiguration.getInt("min-player") +" à " + fileConfiguration.getInt("max-player") +" joueurs)");
				ChatComponent message2 = new ChatComponent("§3 : "+instance.mapVotes.get(entry.getKey())+" votes");
				message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/minigta vote "+entry.getKey() ) );
				message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "§7Voter pour la map "+entry.getKey() ).create() ) );
				instance.sendMsg(ChatComponent.create(message, message2));
			}

		}


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
