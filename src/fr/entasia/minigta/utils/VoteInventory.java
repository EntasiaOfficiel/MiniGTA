package fr.entasia.minigta.utils;

import fr.entasia.apis.menus.MenuClickEvent;
import fr.entasia.apis.menus.MenuCreator;
import fr.entasia.minigta.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Map;

import static fr.entasia.minigta.Main.instance;

public class VoteInventory {

	public static MenuCreator voteMenu = new MenuCreator() {

		@Override
		public void onMenuClick(MenuClickEvent e) {
			for (Map.Entry<String, FileConfiguration> entry : instance.mapFiles.entrySet()) {
				if (e.item.getItemMeta().getDisplayName().contains(entry.getKey())) {
					GPlayer gp = Main.instance.pList.get(e.player.getName());
					if (gp.hasVoted) {
						e.player.sendMessage("§7Vous ne pouvez pas voter 2 fois");
						return;
					}
					System.out.println(e.item.getItemMeta().getDisplayName());
					int vote = Main.instance.mapVotes.get(e.item.getItemMeta().getDisplayName()) + 1;
					gp.vote = e.item.getItemMeta().getDisplayName();
					Main.instance.mapVotes.put(e.item.getItemMeta().getDisplayName(), vote);
					e.player.sendMessage("§7Vous avez voté pour la map " + e.item.getItemMeta().getDisplayName() + " elle a maintenant " + Main.instance.mapVotes.get(e.item.getItemMeta().getDisplayName()) + " votes");
					gp.hasVoted = true;
					gp.p.closeInventory();
				}
			}
		}
	};


	public static void voteMenuOpen(Player p){
		int slot = instance.mapFiles.size()*2;
		while( slot%9!=0){
			slot++;
		}
		Inventory inv = voteMenu.createInv(slot/9,"§7Voter pour la map");
		int nextSlot = 1;

		for(Map.Entry<String, FileConfiguration> entry : instance.mapFiles.entrySet()) {
			FileConfiguration fileConfiguration = entry.getValue();
			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§7" + entry.getKey());
			meta.setLore(Collections.singletonList("§7" + " (recommandée de " + fileConfiguration.getInt("min-player") + " à " + fileConfiguration.getInt("max-player") + " joueurs)"));
			item.setItemMeta(meta);

			inv.setItem(nextSlot, item);

			nextSlot+=2;
		}

		p.openInventory(inv);

	}
}
