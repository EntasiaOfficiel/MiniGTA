package fr.entasia.minigta.listener;

import com.shampaggon.crackshot.events.WeaponHitBlockEvent;
import com.shampaggon.crackshot.events.WeaponPlaceMineEvent;
import fr.entasia.minigta.Main;
import fr.entasia.minigta.utils.BreakedBlock;
import fr.entasia.minigta.utils.GState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class WeaponListener implements Listener {

    @EventHandler
    public void onMinePlace(WeaponPlaceMineEvent e){
        if(e.getMine().getWorld().getName().equals(Main.instance.world)){
            if(Main.instance.MineLocation.contains(e.getMine().getLocation())) return;
            Main.instance.MineLocation.add(e.getMine().getLocation());
        }
    }

    @EventHandler
    public void onPlaceMine(BlockPlaceEvent e){
        Block b = e.getBlockPlaced();
        if(b.getType() == Material.SKULL){
            if(Main.instance.MineLocation.contains(b.getLocation())) return;
            Main.instance.MineLocation.add(b.getLocation());
        }
    }

    @EventHandler
    public void onHitBlock(WeaponHitBlockEvent e){
        if(Main.instance.state != GState.PLAYING && Main.instance.state != GState.ENDING) return;
        if(e.getBlock().getType() == Material.GLASS || e.getBlock().getType() == Material.STAINED_GLASS || e.getBlock().getType()== Material.STAINED_GLASS_PANE || e.getBlock().getType()==Material.THIN_GLASS){
            BreakedBlock gl = new BreakedBlock();
            Block b = e.getBlock();
            gl.loc=b.getLocation();
            gl.material =b.getType();
            gl.data = b.getData();
            Main.instance.GlassBroke.add(gl);
            b.setType(Material.AIR, false);

            gl = new BreakedBlock();
            gl.loc = b.getLocation().add(0, 1, 0);
            if(gl.loc.getBlock().getType()==Material.CARPET){
                b = gl.loc.getBlock();
                gl.material = b.getType();
                gl.data = b.getData();
                Main.instance.GlassBroke.add(gl);
                b.setType(Material.AIR, false);
            }
            e.getPlayer().getWorld().playSound(e.getBlock().getLocation(), Sound.valueOf("block_glass_break".toUpperCase()), 1.0f, 1.0f);


        }
    }



}
