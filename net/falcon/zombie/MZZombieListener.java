package net.falcon.zombie;

import net.falcon.MZStrings;
import net.falcon.data.MZOptions;
import net.falcon.data.MZPlayer;
import net.falcon.data.MZVisibility;
import net.falcon.util.MZUtil;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class MZZombieListener implements Listener {


	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntSpawn(CreatureSpawnEvent e) {
		//blocks all natural spawning
		if (e.getSpawnReason() == SpawnReason.NATURAL) {
			if(e.getEntityType() == EntityType.ZOMBIE) {
				CraftWorld w = (CraftWorld)e.getEntity().getLocation().getWorld();
				w.getHandle().removeEntity(((CraftEntity)e.getEntity()).getHandle());
				Zombie z = MZombie.spawn(e.getLocation());
				return;
			}
			e.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntityType() == EntityType.ZOMBIE && MZOptions.BLOOD) {
			e.getEntity().getWorld().playEffect(e.getEntity().getLocation().add(0, 1d, 0),
					Effect.STEP_SOUND, MZOptions.ZOMBIE_BLOOD?Material.ENDER_STONE:Material.REDSTONE_WIRE);
		}
	}
	

	@EventHandler
	public void onZDeath(EntityDeathEvent e) {
		if(e.getEntityType() == EntityType.ZOMBIE) {
			e.getDrops().clear();
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntTarget(EntityTargetEvent e) {

		if(e.getEntityType() == null || e.getTarget() == null) {
			return;
		}
		if(e.getEntityType() != EntityType.ZOMBIE || e.getTarget().getType() != EntityType.PLAYER) {
			return;
		}

		//TODO this can probably be moved to a target goal?
		Zombie z = (Zombie)e.getEntity();
		if(e.getReason() == TargetReason.TARGET_ATTACKED_ENTITY) {
			if(MZOptions.ZOMBIE_HORDE_EFFECT) {
				awakenHorde(z,(Player)e.getTarget());
			}
		}
	}

	@EventHandler
	public void onPlayerSprint(PlayerToggleSprintEvent e) {
		MZPlayer p = MZUtil.getMZPlayer(e.getPlayer().getName());
		if(e.isSprinting()) {
			p.setVisibility(MZVisibility.HIGH);
		} else {
			p.setVisibility(MZVisibility.MEDIUM);
		}
	}

	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent e) {

		MZPlayer p = MZUtil.getMZPlayer(e.getPlayer().getName());
		if(e.isSneaking()) {
			p.setVisibility(MZVisibility.LOW);
		} else {
			p.setVisibility(MZVisibility.MEDIUM);
		}
	}

	/**
	 * Recursively works through a horde of zombies, alerting any zombies within awareness
	 * Of the player noted.
	 * @param z
	 * @param p
	 */
	public void awakenHorde(Zombie z, Player p) {
		int zombiesAwake = 0;
		for(Entity ent : z.getNearbyEntities(MZOptions.ZOMBIE_HORDE_AWARENESS, MZOptions.ZOMBIE_HORDE_AWARENESS, MZOptions.ZOMBIE_HORDE_AWARENESS)) {
			if(ent.getType() == EntityType.ZOMBIE) {
				if(((CraftEntity)ent).getHandle() instanceof MZombie) {
					MZombie mz = (MZombie)((CraftEntity)ent).getHandle();
					//ent.setVelocity(new Vector(0,.3,.0));
					mz.hordeActive = true;
					zombiesAwake++;
				}
			}
		}
		if(zombiesAwake >= 6) {
			MZUtil.playerThink(p, MZStrings.HORDE_AWAKENED);
		}
	}



}
