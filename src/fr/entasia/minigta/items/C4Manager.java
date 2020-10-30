package fr.entasia.minigta.items;

import fr.entasia.apis.nbt.ItemNBT;
import fr.entasia.apis.nbt.NBTComponent;
import fr.entasia.apis.nbt.NBTTypes;
import fr.entasia.minigta.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class C4Manager {

    public static ItemStack createC4(){

        ItemStack item = new ItemStack(Material.IRON_TRAPDOOR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eC4 '2'");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§eClique droit pour placer");
        lore.add("§eClique gauche pour faire exploser");
        meta.setLore(lore);
        item.setItemMeta(meta);

        NBTComponent nbt = new NBTComponent();
        nbt.setValue(NBTTypes.Boolean, "C4", true);
        nbt.setValue(NBTTypes.String, "loc","");
        nbt.setValue(NBTTypes.Int, "rand", Main.r.nextInt(1024));

        ItemNBT.addNBT(item, nbt);

        return item;
    }
}
