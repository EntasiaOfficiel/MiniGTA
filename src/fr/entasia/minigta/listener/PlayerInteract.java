package fr.entasia.minigta.listener;

import fr.entasia.apis.nbt.ItemNBT;
import fr.entasia.apis.nbt.NBTComponent;
import fr.entasia.apis.nbt.NBTTypes;
import fr.entasia.apis.utils.ItemUtils;
import fr.entasia.minigta.Main;
import fr.entasia.minigta.items.C4Manager;
import fr.entasia.minigta.utils.GPlayer;
import fr.entasia.minigta.utils.VoteInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class PlayerInteract implements Listener {


	@EventHandler
	public void onPlayerUse(PlayerInteractEvent e){
	    Player p = e.getPlayer();
		if(e.getAction().toString().startsWith("LE") || e.getAction().toString().startsWith("RI")) {
			if (e.getItem() != null && ItemUtils.hasName(e.getItem())) {
				GPlayer gp = Main.instance.pList.get(p.getName());
				if(gp==null)return;
				if (e.getItem().getItemMeta().getDisplayName().equals("§cRejoindre l'équipe Rouge !"))
					gp.setTeam("red");
				else if (e.getItem().getItemMeta().getDisplayName().equals("§9Rejoindre l'équipe Bleue !"))
					gp.setTeam("blue");
				else if (e.getItem().getItemMeta().getDisplayName().equals("§d§cRetour au spawn EntaGames"))
					Main.instance.quitPlayer(gp, true);
				else if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7Voter pour la map"))
					VoteInventory.voteMenuOpen(gp.p);
				else if (e.getItem().getItemMeta().getDisplayName().startsWith("§eC4"))
					if(e.getAction().toString().startsWith("LE")){
						ItemStack item = e.getItem();

						NBTComponent nbt = ItemNBT.getNBT(item);
						if(nbt==null)return;
						String loc = (String) nbt.getValue(NBTTypes.String ,"loc");
						if(loc == "")return;

						String[] locs = loc.split(";");
						double x = Double.parseDouble(locs[0].split(",")[0]);
						double y = Double.parseDouble(locs[0].split(",")[1]);
						double z = Double.parseDouble(locs[0].split(",")[2]);
						String worldName = locs[0].split(",")[3];

						Block bloc = new Location(Bukkit.getWorld(worldName),x,y,z).getBlock();

						bloc.setType(Material.AIR);
						Bukkit.getWorld(worldName).createExplosion(x,y,z,5.0F,false,false);

						if(locs.length >1){
							x = Double.parseDouble(locs[1].split(",")[0]);
							y = Double.parseDouble(locs[1].split(",")[1]);
							z = Double.parseDouble(locs[1].split(",")[2]);
							worldName = locs[0].split(",")[3];

							bloc = new Location(Bukkit.getWorld(worldName),x,y,z).getBlock();

							bloc.setType(Material.AIR);
							Bukkit.getWorld(worldName).createExplosion(x,y,z,5.0F,false,false);
						}
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName("§eC4 '2'");

						ArrayList<String> lore = new ArrayList<>();
						lore.add("§eClique droit pour placer");
						lore.add("§eClique gauche pour faire exploser");
						meta.setLore(lore);

						item.setItemMeta(meta);


						nbt = new NBTComponent();
						nbt.setValue(NBTTypes.Boolean, "C4", true);


						nbt.setValue(NBTTypes.String, "loc","");

						ItemNBT.addNBT(item, nbt);

					}
				else return;
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public static void onInvClick(InventoryClickEvent e) {
		if (Main.instance.pList.containsKey(e.getWhoClicked().getName()) && e.getSlotType() == InventoryType.SlotType.ARMOR || Main.instance.pList.containsKey(e.getWhoClicked().getName()) && !Main.instance.hasStarted()) e.setCancelled(true);
	}
}