package fr.entasia.minigta.listener;

import fr.entasia.apis.other.ChatComponent;
import fr.entasia.minigta.Main;
import fr.entasia.minigta.utils.GTAPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(!Main.instance.hasStarted()) return;
		GTAPlayer p = Main.instance.pList.get(e.getPlayer().getName());
		if(p!=null){
			if(e.getMessage().startsWith("!!")){

				e.setMessage(e.getMessage().split("!!")[1]);
				return;
			}
			else if(e.getMessage().startsWith("!")){
				Main.instance.sendMsg(ChatComponent.create(p.getColor()+p.p.getName() + "§7: " + e.getMessage().split("!")[1]));
			}else{
				for(GTAPlayer i: Main.instance.pList.values()){
					if(i.team.equals(p.team))i.p.sendMessage(p.getColor()+p.p.getName() + ": " + e.getMessage());
				}
			}
			e.setCancelled(true);
		}
	}

}
