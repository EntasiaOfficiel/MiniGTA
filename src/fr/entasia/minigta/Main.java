package fr.entasia.minigta;

import com.shampaggon.crackshot.CSUtility;
import fr.entasia.apis.other.ChatComponent;
import fr.entasia.apis.sql.SQLConnection;
import fr.entasia.egtools.Utils;
import fr.entasia.egtools.utils.MoneyUtils;
import fr.entasia.minigta.listener.*;
import fr.entasia.minigta.tasks.GAutoStart;
import fr.entasia.minigta.tasks.GAutoStop;
import fr.entasia.minigta.tasks.GNoPvP;
import fr.entasia.minigta.tasks.GRespawn;
import fr.entasia.minigta.utils.CustomScoreboardManager;
import fr.entasia.minigta.utils.GPlayer;
import fr.entasia.minigta.utils.GState;
import fr.entasia.minigta.utils.BreakedBlock;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;


public class Main extends JavaPlugin {


	public static Main instance;
	public Location waitspawn;
	public Map<String, GPlayer> pList = new HashMap<>();
	public Map<String, Integer> mapVotes = new HashMap<>();
	public Map<String, FileConfiguration> mapFiles = new HashMap<>();
	public Map<Player, CustomScoreboardManager> boards = new HashMap<>();
	public List<String> BlueTeam = new ArrayList<>();
	public List<String> RedTeam = new ArrayList<>();
	public File chestFile = new File(getDataFolder(), "chest.yml");
	public File fileConfig = new File(getDataFolder(), "config.yml");
	public FileConfiguration chestConfig;
	public FileConfiguration config;
	public GState state;
	public GAutoStart GameStarter;
	public GAutoStop GameChrono;
	public CSUtility cs = new CSUtility();
	public World world;
	public FileConfiguration worldConfig;
	public int RedPoint = 0;
	public int BluePoint = 0;
	public Location RedSpawn;
	public Location BlueSpawn;
	public List<Location> MineLocation = new ArrayList<>();
	public List<BreakedBlock> GlassBroke = new ArrayList<>();
	public int timer = 20;
	public static SQLConnection sql;

	@Override
	public void onEnable() {
		try {
			sql = new SQLConnection("entagames","playerdata");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		instance = this;

		saveDefaultConfig();
		if(!chestFile.exists()) {
			try {
				chestFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		config = YamlConfiguration.loadConfiguration(fileConfig);
		chestConfig = YamlConfiguration.loadConfiguration(chestFile);
		System.out.println("Plugin MiniGta activé, par Stargeyt");

		String[] d = config.getString("position.waitpoint").split(",");
		waitspawn = new Location(Bukkit.getWorld(config.getString("position.world")),Double.parseDouble(d[0]),Double.parseDouble(d[1]),Double.parseDouble(d[2]));

		state = GState.WAITING;

		getCommand("minigta").setExecutor(new MiniGtaCmd());

		getServer().getPluginManager().registerEvents(new DamageListener(), this);
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
		getServer().getPluginManager().registerEvents(new WeaponListener(),this);

		for(int i=1; new File(Main.instance.getDataFolder(), "map"+i+".yml").exists(); i++){
			File map = new File(getDataFolder(), "map"+i+".yml");
			FileConfiguration mapConfig= YamlConfiguration.loadConfiguration(map);
			mapFiles.put(mapConfig.getString("map-name"), mapConfig);
			mapVotes.put(mapConfig.getString("map-name"), 0);
		}




	}

	
	@Override
	public void onDisable() {
		System.out.println("Plugin MiniGta desactivé");
	}

	public boolean hasStarted() {
		return state==GState.PLAYING;
	}

	public void startGame() {
		int bestVote=0;
		String bestName="";
		for(Map.Entry<String, Integer> entry : mapVotes.entrySet()){
			String mapName = entry.getKey();
			int mapVote = entry.getValue();
			if(mapVote>=bestVote){
				bestVote=mapVote;
				bestName=mapName;
			}
		}

		worldConfig=mapFiles.get(bestName.split("§7")[1]);
		world=Bukkit.getWorld(worldConfig.getString("world-name"));

		state=GState.PLAYING;
		timer = 450;
		setChest();
		forcejoinplayer();
		GameChrono = new GAutoStop();
		GameChrono.runTaskTimer(this, 0, 20);
		for(Map.Entry<Player, CustomScoreboardManager> sb : boards.entrySet()){
			CustomScoreboardManager scoreboard = sb.getValue();
			scoreboard.sendStartingLine();
		}


		int high = worldConfig.getStringList("spawn.points").size();
		int r1 = (int)(Math.random()*high);
		String[] d = worldConfig.getStringList("spawn.points").get(r1).split(",");
		RedSpawn = new Location(Bukkit.getWorld(worldConfig.getString("world-name")), Double.parseDouble(d[0]), Double.parseDouble(d[1]), Double.parseDouble(d[2]));
		while(true){
			int r2=(int)(Math.random()*high);
			if(r2!=r1){
				d = worldConfig.getStringList("spawn.points").get(r2).split(",");
				BlueSpawn = new Location(Bukkit.getWorld(worldConfig.getString("world-name")), Double.parseDouble(d[0]), Double.parseDouble(d[1]), Double.parseDouble(d[2]));
				break;
			}
		}
		for (GPlayer gp : pList.values()) {
			gp.p.setMaxHealth(40);
			gp.p.setHealth(40);
			gp.p.setGameMode(GameMode.SURVIVAL);
			gp.p.getInventory().clear();

			ItemStack item = new ItemStack(Material.IRON_HELMET);
			ItemMeta meta = item.getItemMeta();
			meta.spigot().setUnbreakable(true);
			item.setItemMeta(meta);
			gp.p.getInventory().setHelmet(item);

			item = new ItemStack(Material.IRON_LEGGINGS);
			item.getItemMeta().spigot().setUnbreakable(true);
			gp.p.getInventory().setLeggings(item);

			item = new ItemStack(Material.IRON_BOOTS);
			item.getItemMeta().spigot().setUnbreakable(true);
			gp.p.getInventory().setBoots(item);

			item = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta chestMeta = (LeatherArmorMeta) item.getItemMeta();
			chestMeta.spigot().setUnbreakable(true);

			ItemStack boussole = new ItemStack(Material.COMPASS);
			ItemMeta compassMeta = boussole.getItemMeta();
			compassMeta.setDisplayName("§7Boussole");
			boussole.setItemMeta(compassMeta);
			gp.p.getInventory().setItem(8,boussole);

			if (gp.team.equals("blue")) {
				gp.p.teleport(BlueSpawn);
				chestMeta.setColor(Color.BLUE);
			}else {
				gp.p.teleport(RedSpawn);
				chestMeta.setColor(Color.RED);
			}
			item.setItemMeta(chestMeta);
			gp.p.getInventory().setChestplate(item);

		}
	}

	public void quitPlayer(GPlayer gp, boolean manual) {
		if(manual){
			Utils.tpSpawn(gp.p);
			gp.p.sendMessage("Tu as quitté la partie !");
		}
		String c = gp.getColor();
		pList.remove(gp.p.getName());
		BlueTeam.remove(gp.p.getName());
		RedTeam.remove(gp.p.getName());
		sendMsg(ChatComponent.create(c+gp.p.getDisplayName()+"§7 a quitté la partie !"));
		if(boards.containsKey(gp.p)){
			new BukkitRunnable() {
				public void run() {
					gp.p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
			}.runTaskLater(Main.instance, 20);
			boards.remove(gp.p);

		}
		for(Map.Entry<Player, CustomScoreboardManager> sb : boards.entrySet()){
			CustomScoreboardManager scoreboard = sb.getValue();
			scoreboard.refreshWaiting();
		}
		if(state==GState.PLAYING) {
			if (RedTeam.size() == 0 || BlueTeam.size() == 0) {
				GameChrono.cancel();
				sendMsg(ChatComponent.create("§7La partie à été arretée suite à un nombre insuffisant de joueurs restants"));
				endGame();
			}
		}else if(state==GState.STARTING){
			for(Map.Entry<Player, CustomScoreboardManager> sign : boards.entrySet()){
				sign.getKey().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
			for(Map.Entry<String,Integer> entry: mapVotes.entrySet()){
				if(entry.getKey().equalsIgnoreCase(gp.vote)){
					int value = entry.getValue();
					entry.setValue(value-1);
					gp.vote="";
				}
			}
			if(instance.pList.size() < instance.config.getInt("config.minPlayers")){
				GameStarter.cancel();

				sendMsg(ChatComponent.create("§7Lancement de la partie annulé !"));
				for(GPlayer gp2 : instance.pList.values()) {
				    gp.p.getInventory().setItem(0,null);
					gp2.p.setLevel(0);
				}
				state=GState.WAITING;
			}
			boards.clear();
			for(GPlayer gplayer : instance.pList.values()){
				Player p = gplayer.p;
				CustomScoreboardManager scoreboard = new CustomScoreboardManager(p);
				scoreboard.sendWaitingLine();
				scoreboard.set();
				boards.put(p,scoreboard);
			}

		}
	}

	public void sendMsg(BaseComponent[] msg) {
		for(GPlayer i : pList.values()){
			i.p.sendMessage(msg);
		}

	}
	public void joinPlayer(Player p) {
		pList.put(p.getName(), new GPlayer(p));
		for(Map.Entry<Player, CustomScoreboardManager> sb : boards.entrySet()){
			CustomScoreboardManager scoreboard = sb.getValue();
			scoreboard.refreshWaiting();
		}
		CustomScoreboardManager scoreboard = new CustomScoreboardManager(p);
		scoreboard.sendWaitingLine();
		scoreboard.set();
		boards.put(p,scoreboard);


		p.sendMessage("Vous avez rejoint la partie !");
		Utils.reset(p);
		p.teleport(waitspawn);
		ChatComponent t = new ChatComponent("§8[§7Oui§8]$7");

		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minigta pack"));
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponent.create("§7Clique pour activer le pack !")));
		p.spigot().sendMessage(ChatComponent.create(new ChatComponent("§7Voulez-vous télécharger le ressource pack : "), t, new ChatComponent("  " + "§8[§7Non§8]§7")));

		instance.sendMsg(ChatComponent.create("§7"+p.getName() + " a rejoint la partie !"));


		Inventory Inv= p.getInventory();
		ItemStack item = new ItemStack(Material.LAPIS_BLOCK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Rejoindre l'équipe Bleue !");
		item.setItemMeta(meta);
		Inv.setItem(2, item);

		item = new ItemStack(Material.REDSTONE_BLOCK);
		meta = item.getItemMeta();
		meta.setDisplayName("§cRejoindre l'équipe Rouge !");
		item.setItemMeta(meta);
		Inv.setItem(6, item);

		item = new ItemStack(Material.BED);
		meta = item.getItemMeta();
		meta.setDisplayName("§d§cRetour au spawn EntaGames");
		item.setItemMeta(meta);
		Inv.setItem(8, item);



		p.setGameMode(GameMode.ADVENTURE);

		if(state==GState.WAITING) {

			if(config.getInt("config.minPlayers") <= pList.size()) {
				GameStarter = new GAutoStart();
				GameStarter.runTaskTimer(this, 0, 20);
				state = GState.STARTING;
                for(Map.Entry<String, GPlayer> entry: pList.entrySet()){
                    GPlayer gp = entry.getValue();
                    item = new ItemStack(Material.PAPER);
                    meta = item.getItemMeta();
                    meta.setDisplayName("§7Voter pour la map");
                    item.setItemMeta(meta);
                    gp.p.getInventory().setItem(0,item);
                }
				sendMsg(ChatComponent.create("§7La partie va commencer dans 1 minute !"));
				for(int i=1; new File(Main.instance.getDataFolder(), "map"+i+".yml").exists(); i++){
					File map = new File(getDataFolder(), "map"+i+".yml");
					FileConfiguration mapConfig= YamlConfiguration.loadConfiguration(map);
					mapFiles.put(mapConfig.getString("map-name"), mapConfig);
					mapVotes.put(mapConfig.getString("map-name"), 0);
				}
			}
		}
	}

	private void setChest() {

		int number = 0;
		for(int i=1; worldConfig.get("chest." +i) != null; i++) {
			String[] d = worldConfig.getString("chest."+i).split(",");
			Location location = new Location(Bukkit.getWorld(worldConfig.getString("world-name")),Double.parseDouble(d[0]),Double.parseDouble(d[1]),Double.parseDouble(d[2]) );
			location.getBlock().setType(Material.CHEST);
			
			Chest chest = (Chest) location.getBlock().getState();
			Inventory inv =chest.getInventory();
			inv.clear();
			for(int i2=1; worldConfig.get("weapon." +i2) != null; i2++) {
				
				if(number <= 4) {
					if(Math.random() * 100 < worldConfig.getInt("weapon." + i2 +".chance")) {
						number++;
						int test = new Random().nextInt(27);
						while(test == 0 || inv.getItem(test) != null) {
							test = new Random().nextInt(27);
						}
						int amount=1;
						int teste = worldConfig.getInt("weapon."+i2+".amount");
						if(teste > 0){
							amount=teste;
						}

						ItemStack item =cs.generateWeapon(worldConfig.getString("weapon." + i2 + ".name"));
						item.setAmount(amount);
						inv.setItem(test, item);
					}
				}
				
			
			}

			while(number<1){
				for(int i2=1; worldConfig.get("weapon." +i2) != null; i2++) {

					if(number <= 4) {
						if(Math.random() * 100 < worldConfig.getInt("weapon." + i2 +".chance")) {
							number++;
							int test = new Random().nextInt(27);
							while(test == 0 || inv.getItem(test) != null) {
								test = new Random().nextInt(27);
							}
							int amount=1;
							int teste = worldConfig.getInt("weapon."+i2+".amount");
							if(teste > 0){
								amount=teste;
							}
							ItemStack item =cs.generateWeapon(worldConfig.getString("weapon." + i2 + ".name"));
							item.setAmount(amount);
							inv.setItem(test, item);

						}
					}


				}
			}
			number = 0;
			
		}
		
		
	}
	private void forcejoinplayer() {
		for(GPlayer pi : pList.values()) {
			if(!RedTeam.contains(pi.p.getName())&&!BlueTeam.contains(pi.p.getName())) {
				if(RedTeam.size() < BlueTeam.size()) {
					pi.setTeam("red");
				} else {
					pi.setTeam("blue");
				}
			}
		}
	}

	public void eliminate(GPlayer p) {
			if(p.team.equals("blue"))RedPoint++;
			else BluePoint++;
			p.death++;
			p.p.setHealth(p.p.getMaxHealth());
			p.p.setFoodLevel(20);
			p.p.setFireTicks(0);
			PotionEffect effect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5, 100);
			p.p.addPotionEffect(effect);
			p.p.teleport(BlueSpawn);
			for(Map.Entry<Player, CustomScoreboardManager> sign : boards.entrySet()){
				sign.getValue().refreshScore();
			}
			if(timer > 5){
				p.p.setGameMode(GameMode.SPECTATOR);
				new GRespawn(p.p).runTaskTimer(this, 0, 20);
			}else{
				if(p.team.equals("blue"))p.p.teleport(BlueSpawn);
				else p.p.teleport(RedSpawn);
		}
		
		
	}



	public void endGame() {
		int bestKill=0;
		String bestName ="";
		String lostTeam="";
		if(BluePoint > RedPoint){
			lostTeam="red";
		}else if (BluePoint < RedPoint){
			lostTeam="blue";
		}
		state = GState.ENDING;
		for (GPlayer gp : pList.values()){
			gp.p.getActivePotionEffects().clear();
			Utils.tpSpawn(gp.p);
			if(gp.pack){
				gp.p.setResourcePack("https://www.dropbox.com/s/jcwneplbnrsv651/nopack.zip?dl=1","1E7D4C9E43EBEA35C7BF730200121EDC");
			}

			int n;
			if(gp.kill>=bestKill) {
				bestName = gp.p.getName();
				bestKill = gp.kill;
			}


			if(gp.team.equals(lostTeam)){
				gp.p.sendMessage("§cDéfaite ...");
				n = config.getInt("config.coins.loose");
			}else if(lostTeam.equals("")){
				gp.p.sendMessage("§9Egal§cité !");
				n = config.getInt("config.coins.egalite");
			}else{
				gp.p.sendMessage("§9Victoire !");
				n = config.getInt("config.coins.win");
			}
			MoneyUtils.addMoney(gp.p.getUniqueId(), n);

			try{
				sql.checkConnect();
				PreparedStatement ps = sql.connection.prepareStatement("update entagames set gta_kill=gta_kill+?, gta_death=gta_death+? where uuid = ?");
				ps.setInt(1, gp.kill);
				ps.setInt(2, gp.death);
				ps.setString(3, gp.p.getUniqueId().toString());
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			gp.p.sendMessage("§7Vous avez gagné "+n+" §7coins !");
		}



		for(GPlayer gp : pList.values()){
			gp.p.sendMessage("§7Vous avez fait "+gp.kill+" kills et êtes mort "+gp.death+" fois");
			gp.p.sendMessage("§7Le meilleur joueur est "+bestName+" avec un total de "+bestKill+" kills");
		}
		for(Map.Entry<Player, CustomScoreboardManager> sign : boards.entrySet()){
			sign.getKey().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}

		for (Location loc : MineLocation) {
			loc.getBlock().setType(Material.AIR);
		}

		for(BreakedBlock bl : GlassBroke){
			Block b = bl.loc.getBlock();
			b.setType(bl.material, false);
			b.setData(bl.data, false);
		}


		GlassBroke.clear();
		MineLocation.clear();
		boards.clear();
		pList.clear();
		RedTeam.clear();
		BlueTeam.clear();
		BluePoint = 0;
		RedPoint = 0;
		state = GState.WAITING;

	}

	public static void respawn(Player p){
		if(Main.instance.BlueTeam.contains(p.getName())){
			p.teleport(Main.instance.BlueSpawn);
		}
		else {
			p.teleport(Main.instance.RedSpawn);
		}
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		for(GPlayer gp: Main.instance.pList.values()){
			if(gp.p.getDisplayName().equalsIgnoreCase(p.getDisplayName())){
				gp.noPvp=true;
			}
		}

		GNoPvP noPvP = new GNoPvP(p);
		noPvP.runTaskTimer(Main.instance,0,20);

	}
	public byte[] calcSHA1Hash(String resourcepackUrl) {
		try {
			URL url = new URL(resourcepackUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			if (connection.getContentLength() <= 0) {
				return null;
			}
			byte[] resourcePackBytes = new byte[connection.getContentLength()];
			InputStream in = connection.getInputStream();

			int b;
			int i = 0;
			while ((b = in.read()) != -1) {
				resourcePackBytes[i] = (byte) b;
				i++;
			}

			in.close();

			if (resourcePackBytes != null) {
				MessageDigest md = MessageDigest.getInstance("SHA-1");
				return md.digest(resourcePackBytes);
			}
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
