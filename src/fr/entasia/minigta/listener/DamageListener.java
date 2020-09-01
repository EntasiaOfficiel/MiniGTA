package fr.entasia.minigta.listener;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.shampaggon.crackshot.events.WeaponPrepareShootEvent;
import fr.entasia.apis.other.ChatComponent;
import fr.entasia.minigta.Main;
import fr.entasia.minigta.tasks.FireExtinguishTask;
import fr.entasia.minigta.utils.GPlayer;
import fr.entasia.minigta.utils.GState;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class DamageListener implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p1 = (Player) e.getEntity();
			if(e.getDamager() instanceof Snowball) {
				Snowball s = (Snowball) e.getDamager();
				if(s.getShooter() instanceof Player) {
					Player p2 = (Player) s.getShooter();
					if(p1.equals(p2)) {
						e.setCancelled(true);

						p1.setVelocity(new Vector(0, 0, 0));
					}
				}
			}
		}
	}


	@EventHandler
	public void pvp(EntityDamageByEntityEvent e) {
		if(e.getEntity().getWorld()==Main.instance.world&&e.getEntity() instanceof Player && Main.instance.hasStarted()) {
			GPlayer gp = Main.instance.pList.get(e.getEntity().getName());
			GPlayer gp2 = Main.instance.pList.get(e.getDamager().getName());
			if(gp!=null&&gp2!=null&&gp.team.equals(gp2.team)&&gp2.p.getInventory().getItemInMainHand().getType()!=Material.IRON_SWORD){
				e.setCancelled(true);
				gp2.p.sendMessage("§cVous êtes dans la même équipe !");
			}
		}
	}

	@EventHandler
	public void blocIgnite(BlockIgniteEvent e){
		if(e.getBlock().getWorld().getName().equals(Main.instance.config.getString("position.world"))){
			if(Main.instance.state== GState.PLAYING || Main.instance.state==GState.STARTING){
				FireExtinguishTask task = new FireExtinguishTask(e.getBlock());
				task.runTaskTimer(Main.instance,0,20);
			}
		}
	}


	@EventHandler
	public void damage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player){
			GPlayer gp = Main.instance.pList.get(e.getEntity().getName());
			if(gp==null)return;
			if(Main.instance.hasStarted()){
				if(e.getCause()==EntityDamageEvent.DamageCause.PROJECTILE||
						e.getCause()==EntityDamageEvent.DamageCause.ENTITY_ATTACK||e.getCause()== EntityDamageEvent.DamageCause.BLOCK_EXPLOSION||
				e.getCause()==EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)return;
				if(gp.p.getHealth()>e.getDamage())return;
				e.setCancelled(true);
				Main.instance.eliminate(gp);
				Main.instance.sendMsg(ChatComponent.create(gp.getColor()+gp.p.getDisplayName()+"§7 est mort"));
			}else e.setCancelled(true);
		}
	}

	@EventHandler
	public void weapon(WeaponDamageEntityEvent e) {

		if(e.getVictim() instanceof Player && Main.instance.hasStarted()){

			GPlayer victim = Main.instance.pList.get(e.getVictim().getName());
			GPlayer attacker = Main.instance.pList.get(e.getPlayer().getName());
			if(victim!=null&&attacker!=null){
				if(victim.team.equals(attacker.team)) {
					if (attacker != victim) attacker.p.sendMessage("§cVous êtes dans la même équipe !");

					e.setCancelled(true);
				}else{

					if(victim.noPvp){
						e.setCancelled(true);
						attacker.p.sendMessage(new TextComponent("§7Merci de ne pas spawn kill"));
						return;
					}
					if(victim.p.getHealth()<=e.getDamage()){
						e.setCancelled(true);
						Main.instance.eliminate(victim);
						attacker.kill++;
						attacker.p.playSound(attacker.p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,5,5);
						if(e.getWeaponTitle().equalsIgnoreCase("Couteau")){
							Main.instance.sendMsg(ChatComponent.create(victim.getColor()+victim.p.getDisplayName()+"§7 s'est fait poignarder par "+attacker.getColor()+attacker.p.getDisplayName()));
						} else if(e.getWeaponTitle().equalsIgnoreCase("C4") || e.getWeaponTitle().startsWith("Gre") || e.getWeaponTitle().startsWith("Fumi")){
							Main.instance.sendMsg(ChatComponent.create(victim.getColor()+victim.p.getDisplayName()+"§7 s'est fait exploser par "+attacker.getColor()+attacker.p.getDisplayName()));
						}

						else Main.instance.sendMsg(ChatComponent.create(victim.getColor()+victim.p.getDisplayName()+"§7 s'est fait canarder par "+attacker.getColor()+attacker.p.getDisplayName()+"§7 à l'aide d'un " + e.getWeaponTitle()));
					}
				}
			}
		}
	}



	@EventHandler
	public void onShoot(WeaponPrepareShootEvent e) {
		if(e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
			e.setCancelled(true);
		}
	}

}
