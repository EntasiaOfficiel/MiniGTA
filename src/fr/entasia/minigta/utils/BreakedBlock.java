package fr.entasia.minigta.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BreakedBlock {

	public Location loc;
	public Material material;

	public BreakedBlock(Block b){
		loc = b.getLocation();
		material = b.getType();
	}

}
