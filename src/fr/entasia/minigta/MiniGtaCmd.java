package fr.entasia.minigta;

import fr.entasia.minigta.utils.GPlayer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class MiniGtaCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		Player p = (Player) sender;
		if (args.length == 0) p.sendMessage("Syntaxe --> /minigta (join/leave)");
		else {
			switch (args[0].toLowerCase()) {
				case "join": {
					if (Main.instance.pList.containsKey(p.getName()))
						p.sendMessage("Vous êtes deja dans la partie");
					else if (Main.instance.hasStarted()) {
						p.sendMessage("La partie a déjà commencé");
						return false;
					} else Main.instance.joinPlayer(p);
					break;
				}
				case "leave": {
					GPlayer gp = Main.instance.pList.get(p.getName());
					if (gp != null) Main.instance.quitPlayer(gp, true);
					else sender.sendMessage("Vous n'êtes pas dans une partie");
					break;
				}
				case "chest": {
					if (p.hasPermission("gta.chest")) {
						Location l = p.getLocation();
						int x = (int) l.getX();
						int y = (int) l.getY();
						int z = (int) l.getZ();
						String string = x + "," + y + "," + z;
						ConfigurationSection cs = Main.instance.chestConfig.getConfigurationSection("chest");
						int t = cs.getKeys(false).size() + 1;
						String s = Integer.toString(t);
						Main.instance.chestConfig.set("chest." + s, string);
						try {
							Main.instance.chestConfig.save(Main.instance.chestFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				}
				case "reload": {
					Main.instance.chestFile = Main.instance.chestFile.getAbsoluteFile();
					Main.instance.chestConfig = YamlConfiguration.loadConfiguration(Main.instance.chestFile);
					Main.instance.reloadConfig();
					Main.instance.mapVotes.clear();
					Main.instance.mapFiles.clear();
					for (int i = 1; new File(Main.instance.getDataFolder(), "map" + i + ".yml").exists(); i++) {
						File map = new File(Main.instance.getDataFolder(), "map" + i + ".yml");
						FileConfiguration mapConfig = YamlConfiguration.loadConfiguration(map);
						Main.instance.mapFiles.put(mapConfig.getString("map-name"), mapConfig);
						Main.instance.mapVotes.put(mapConfig.getString("map-name"), 0);
					}
					break;
				}

				case "pack": {
					GPlayer gp = Main.instance.pList.get(p.getName());
					if (gp == null) {
						p.sendMessage("§7Tu n'es pas dans une partie de MiniGTA !");
						return true;
					} else {

						p.setResourcePack("https://www.dropbox.com/s/yi9s99ukehwcrbl/minigta-pack.zip?dl=1", "80D288EFC6C4AD12444C3C7618417F03");
						p.sendMessage("§7Tu as téléchargé le ressource pack");
						gp.pack = true;
					}
					break;
				}


				default: {
					p.sendMessage("§cArgument incorrect !");
					break;
				}
			}
		}
		return true;
	}
}
