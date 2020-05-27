package fr.entasia.minigta.listener;

import fr.entasia.minigta.Main;
import fr.entasia.minigta.utils.GPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener {


	@EventHandler
	public void onPlayerUse(PlayerInteractEvent e){
	    Player p = e.getPlayer();
		if(e.getAction().toString().startsWith("LE") || e.getAction().toString().startsWith("RI")) {
			if (e.getItem() != null && e.getItem().getItemMeta() != null && e.getItem().getItemMeta().getDisplayName() != null) {
				GPlayer gp = Main.instance.pList.get(p.getName());
				if(gp==null)return;
				if (e.getItem().getItemMeta().getDisplayName().equals("§cRejoindre l'équipe Rouge !"))
					gp.setTeam("red");
				else if (e.getItem().getItemMeta().getDisplayName().equals("§9Rejoindre l'équipe Bleue !"))
					gp.setTeam("blue");
				else if (e.getItem().getItemMeta().getDisplayName().equals("§d§cRetour au spawn EntaGames"))
					Main.instance.quitPlayer(gp, true);
				else return;
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public static void onInvClick(InventoryClickEvent e) {
		if (e.getWhoClicked().getWorld().getName().equals(Main.instance.world)
				&& !Main.instance.hasStarted()&& Main.instance.pList.containsKey(e.getWhoClicked().getName())) e.setCancelled(true);
	}
}