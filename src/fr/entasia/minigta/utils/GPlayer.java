package fr.entasia.minigta.utils;

import fr.entasia.minigta.Main;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class GPlayer {

	public Player p;
	public String team="";
	public int kill=0;
	public int death=0;
	public boolean pack=false;
	public boolean hasVoted=false;
	public String vote = "";

	public GPlayer(Player p){
		this.p = p;
	}

	public void setTeam(String team){
		ItemStack a = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta aMeta = (LeatherArmorMeta) a.getItemMeta();
		if(team.equals("blue")){
			aMeta.setColor(Color.BLUE);
			if(this.team.equals("blue")){
				p.sendMessage("§9Vous êtes déja dans l'équipe bleue !");
				return;
			}
			int i=0;
			if(this.team.equals(""))i=1;

			if(Main.instance.BlueTeam.size() >= Main.instance.RedTeam.size()+i){
				p.sendMessage("§9L'équipe bleue à déja assez de joueurs !");
				return;
			}

			Main.instance.RedTeam.remove(p.getName());
			Main.instance.BlueTeam.add(p.getName());
			p.sendMessage("§9Vous avez rejoint l'équipe bleue !");

		}else {
			aMeta.setColor(Color.RED);
			if(this.team.equals("red")){
				p.sendMessage("§cVous êtes déja dans l'équipe rouge !");
				return;
			}
			int i=0;
			if(this.team.equals(""))i=1;

			if(Main.instance.RedTeam.size() >= Main.instance.BlueTeam.size()+i){
				p.sendMessage("§cL'équipe rouge à déja assez de joueurs !");
				return;
			}


			Main.instance.BlueTeam.remove(p.getName());
			Main.instance.RedTeam.add(p.getName());
			p.sendMessage("§cVous avez rejoint l'équipe rouge !");
		}
		a.setItemMeta(aMeta);
		p.getInventory().setChestplate(a);
		this.team = team;
}

	public int getDeath(){
		return death;
	}

	public int getKill(){
		return kill;
	}

	public boolean hasVoted(){ return hasVoted();}

	public String getColor() {
		if(team.equals("blue"))return"§9";
		else if(team.equals("red")) return "§c";
		else return "§7";
	}

	public String getVote(){
		return vote;
	}

}