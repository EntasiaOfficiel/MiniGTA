package fr.entasia.minigta;

import fr.entasia.minigta.utils.GPlayer;
import fr.entasia.minigta.utils.GState;
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
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(args.length ==1) {
				if(args[0].equalsIgnoreCase("join")) {
					if(Main.instance.pList.containsKey(p)) p.sendMessage("Vous êtes deja dans la partie");
					else if(Main.instance.hasStarted()) {
						p.sendMessage("La partie a déjà commencé");
						return false;
					}else Main.instance.joinPlayer(p);
				}else if(args[0].equalsIgnoreCase("leave")) {
					GPlayer gp = Main.instance.pList.get(p.getName());
					if(gp!=null) Main.instance.quitPlayer(gp, true);
					else sender.sendMessage("Vous n'êtes pas dans une partie");
				}else if(args[0].equalsIgnoreCase("chest")) {
					if(p.hasPermission("gta.chest")) {
						Location l = p.getLocation();
						int x = (int)l.getX();
						int y = (int)l.getY();
						int z = (int)l.getZ();
						String string = x+","+y+","+z;
						ConfigurationSection cs = Main.instance.chestConfig.getConfigurationSection("chest");
						int t= cs.getKeys(false).size() + 1;
						String s = Integer.toString(t);
						Main.instance.chestConfig.set("chest." + s , string);
						try {
							Main.instance.chestConfig.save(Main.instance.chestFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else if(args[0].equalsIgnoreCase("reload")) {
					Main.instance.chestFile = Main.instance.chestFile.getAbsoluteFile();
					Main.instance.fileConfig = Main.instance.fileConfig.getAbsoluteFile();
					Main.instance.chestConfig = YamlConfiguration.loadConfiguration(Main.instance.chestFile);
					Main.instance.config = YamlConfiguration.loadConfiguration(Main.instance.fileConfig);
					Main.instance.mapVotes.clear();
					Main.instance.mapFiles.clear();
					for(int i=1; new File(Main.instance.getDataFolder(), "map"+i+".yml").exists(); i++){
						File map = new File(Main.instance.getDataFolder(), "map"+i+".yml");
						FileConfiguration mapConfig= YamlConfiguration.loadConfiguration(map);
						Main.instance.mapFiles.put(mapConfig.getString("map-name"), mapConfig);
						Main.instance.mapVotes.put(mapConfig.getString("map-name"), 0);
					}

				}else if(args[0].equalsIgnoreCase("red")||args[0].equalsIgnoreCase("blue")) {
					GPlayer gp = Main.instance.pList.get(p.getName());
					if(gp==null){
						p.sendMessage("§7Tu n'est pas en partie !");
						return true;
					}
					gp.setTeam(args[0]);
				}else if(args[0].equalsIgnoreCase("pack")) {
					GPlayer gp = Main.instance.pList.get(p.getName());
					if(gp==null){
						p.sendMessage("§7Tu n'est pas dans une partie de MiniGTA !");
						return true;
					}else{
						p.setResourcePack("https://files.entasia.fr/servers/entagames/minigta.zip");
						p.sendMessage("§7Vous avez téléchargé le ressource pack");
						gp.pack = true;
					}

				}else p.sendMessage("§cArgument incorrect !");

			}else if(args[0].equalsIgnoreCase("vote")){
				if(args.length != 2){
					p.sendMessage("Syntaxe --> /minigta (join/leave)");
					return false;
				}
				if(Main.instance.state != GState.STARTING){
					p.sendMessage("§7Vous ne pouvez pas voter pour une map maintenant");
					return false;
				}
				if(Main.instance.mapVotes.containsKey(args[1])){
					GPlayer gp = Main.instance.pList.get(p.getName());
					if(gp.hasVoted){ p.sendMessage("§7Vous ne pouvez pas voter 2 fois"); return false;}
					int vote = Main.instance.mapVotes.get(args[1]) +1;
					gp.vote = args[1];
					Main.instance.mapVotes.put(args[1], vote);
					p.sendMessage("§7Vous avez voté pour la map "+args[1]+" elle a maintenant "+Main.instance.mapVotes.get(args[1])+" votes");
					gp.hasVoted=true;
				}else{
					p.sendMessage("§7Map incorrecte");
					return false;
				}

			}else p.sendMessage("Syntaxe --> /minigta (join/leave)");
			return true;
		} else return false;
	}

}
