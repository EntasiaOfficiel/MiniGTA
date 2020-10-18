package fr.entasia.minigta.listener;

import fr.entasia.apis.nbt.ItemNBT;
import fr.entasia.apis.nbt.NBTComponent;
import fr.entasia.apis.nbt.NBTTypes;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.TrapDoor;

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
				if(b.getType()==Material.IRON_TRAPDOOR){
					e.setCancelled(false);
					if(!Main.instance.mineLocs.contains(b.getLocation())){
						Main.instance.mineLocs.add(b.getLocation());
					}

					ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
					int nbrC4 = Integer.parseInt(item.getItemMeta().getDisplayName().split("'")[1]);
					if(nbrC4 <=0){
						e.setCancelled(true);
						return;
					}

					if(b.getWorld().getBlockAt(b.getLocation().add(0,-1,0)).getType().equals(Material.AIR)){
						TrapDoor trapDoor = (TrapDoor) b.getState().getData();
						trapDoor.setOpen(true);
					}
					String[] name = item.getItemMeta().getDisplayName().split("'");
					name[1]= "'"+ (nbrC4 - 1) +"'";

					String finalName = String.join(" ",name);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(finalName);
					item.setItemMeta(meta);

					double x = b.getLocation().getX();
					double y = b.getLocation().getY();
					double z = b.getLocation().getZ();
					String worldName = b.getWorld().getName();
					String newLoc = x+","+y+","+z+","+worldName;

					NBTComponent nbt = ItemNBT.getNBT(item);

					if(nbt==null)return;
                    System.out.println("Test1");
					String location = (String) nbt.getValue(NBTTypes.String,"loc");
					if(location == null)return;
                    System.out.println("Test2");
					String[] locs = location.split(";");
					String finalLocs = "";

					if(nbrC4==1){
						finalLocs = locs[0]+";"+newLoc;
					}else if(nbrC4==2){
						finalLocs = newLoc+";";
					}


					nbt.setValue(NBTTypes.String,"loc",finalLocs);
					ItemNBT.addNBT(item, nbt);

					e.setCancelled(false);

				}
		}
	}


}
