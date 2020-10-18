package fr.entasia.minigta.listener;

import com.destroystokyo.paper.MaterialTags;
import com.shampaggon.crackshot.events.WeaponHitBlockEvent;
import com.shampaggon.crackshot.events.WeaponPlaceMineEvent;
import fr.entasia.minigta.Main;
import fr.entasia.minigta.utils.BreakedBlock;
import fr.entasia.minigta.utils.GState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class WeaponListener implements Listener {

	@EventHandler
	public void onMinePlace(WeaponPlaceMineEvent e){
		if(e.getMine().getWorld()==Main.instance.world){
			if(Main.instance.mineLocs.contains(e.getMine().getLocation())) return;
			Main.instance.mineLocs.add(e.getMine().getLocation());
		}
	}



	@EventHandler
	public void onHitBlock(WeaponHitBlockEvent e){
		if(Main.instance.state != GState.PLAYING && Main.instance.state != GState.ENDING) return;
		if(MaterialTags.GLASS.isTagged(e.getBlock().getType())||MaterialTags.GLASS_PANES.isTagged(e.getBlock().getType())){
			BreakedBlock bb = new BreakedBlock(e.getBlock());
			Main.instance.GlassBroke.add(bb);
			e.getBlock().setType(Material.AIR, false);

			Block b = e.getBlock().getRelative(BlockFace.UP);
			if(Tag.CARPETS.isTagged(b.getType())){
				bb = new BreakedBlock(b);
				Main.instance.GlassBroke.add(bb);
				b.setType(Material.AIR, false);
			}
			e.getPlayer().getWorld().playSound(e.getBlock().getLocation(), Sound.valueOf("block_glass_break".toUpperCase()), 1.0f, 1.0f);


		}
	}
}
