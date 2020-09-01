package fr.entasia.minigta.listener;

import fr.entasia.minigta.Main;
import fr.entasia.minigta.utils.GState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {



	@EventHandler
	public void WorldChange(PlayerChangedWorldEvent e){
		if(e.getFrom()==Main.instance.world) {
			if (Main.instance.pList.containsKey(e.getPlayer().getName())) {
				if (Main.instance.state == GState.ENDING) return;
				Main.instance.quitPlayer(Main.instance.pList.get(e.getPlayer().getName()), false);
			}
		}
	}
	
	
	
	@EventHandler
	public void PlayerTP(PlayerTeleportEvent event){
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE)
	    	if(Main.instance.pList.containsKey(event.getPlayer().getName())){
	    		event.setCancelled(true);
	    		event.getPlayer().sendMessage("Vous êtes dans une partie , ne partez pas !");
	        }
	}
	
	
	
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(Main.instance.pList.containsKey(e.getPlayer().getName())){
			if(Main.instance.state == GState.ENDING) return;
			Main.instance.quitPlayer(Main.instance.pList.get(e.getPlayer().getName()), false);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		if(e.getPlayer().getWorld() == Main.instance.world&&
				Main.instance.pList.containsKey(e.getPlayer().getName()) ){
				Block b = e.getBlockPlaced();
				if(b.getType().equals(Material.SKULL)){
					e.setCancelled(false);
				}
		}
	}


}
