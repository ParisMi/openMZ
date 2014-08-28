package net.falcon.item;

import net.falcon.MZStrings;
import net.falcon.data.MZHealingState;
import net.falcon.data.MZOptions;
import net.falcon.data.MZPlayer;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MZBandage extends MZItemTemplate {

	@Override
	public void onDealDamage(EntityDamageByEntityEvent e) {
		if(e.getEntityType() != EntityType.PLAYER) {
			return;
		}
		e.setCancelled(true);
		Player p = (Player)e.getDamager();
		Player healed = (Player)e.getEntity();
		MZHealingState healState = MZUtil.getMZHealingState(healed);
		if(healState == MZHealingState.HEAL_COOLDOWN) {
			MZUtil.playerThink(p, MZStrings.HEALING_COOLDOWN);
			return;
		}
		MZUtil.playerThink(p, "Started healing " + healed.getName());
		MZUtil.playerThink(healed, p.getName() + " is starting to heal me...");
		
		MZUtil.setMZHealingState(healed, MZHealingState.REG_HEAL);
	}
	
	
	@Override
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_AIR) {
			MZPlayer p = MZUtil.getMZPlayer(e.getPlayer().getName());
			e.getPlayer().getInventory().setItemInHand(null);
			if(p.isBleeding()) {
				p.setBleeding(false);
			} else {
				if(e.getPlayer().getHealth() <= 20 - MZOptions.BANDAGE_HEAL) {
					MZUtil.playerThink(e.getPlayer(), MZStrings.BANDAGE_HEAL);
					e.getPlayer().setHealth(e.getPlayer().getHealth() + MZOptions.BANDAGE_HEAL);
				}
			}
		}
	}

	@Override
	public String getName() {
		return ChatColor.BLUE + "Bandage";
	}

	@Override
	public Material getType() {
		return Material.PAPER;
	}


}
