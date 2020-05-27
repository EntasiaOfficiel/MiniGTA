package fr.entasia.minigta.tasks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class FireExtinguishTask  extends BukkitRunnable {

    public Block b;
    public int timer = 30;

    public FireExtinguishTask(Block b){
        this.b = b;
    }

    @Override
    public void run() {
        timer--;
        if(timer <= 0){
            b.setType(Material.AIR);
            cancel();
        }
    }
}
