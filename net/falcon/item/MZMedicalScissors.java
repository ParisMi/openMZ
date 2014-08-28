package net.falcon.item;

import net.falcon.MZStrings;
import net.falcon.OpenMZ;
import net.falcon.data.MZHealingState;
import net.falcon.data.MZOptions;
import net.falcon.util.MZUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MZMedicalScissors extends MZItemTemplate {


	@Override
	public void onDealDamage(EntityDamageByEntityEvent e) {
		if(e.getEntityType() != EntityType.PLAYER) {
			return;
		}
		e.setCancelled(true);
		Player p = (Player)e.getDamager();
		Player healed = (Player)e.getEntity();
		MZHealingState healState = MZUtil.getMZHealingState(healed);
		switch(healState) {
		case HEAL_COOLDOWN:
			MZUtil.playerThink(p, MZStrings.HEALING_COOLDOWN);
			break;

		case DEFAULT:
			MZUtil.playerThink(p, MZStrings.HEALING_HELP);
			break;

		case REG_HEAL:
		case EXTRA_HEAL:
			MZUtil.playerThink(p, "You successfully healed " + healed.getName() + ".");
			Integer dur = healState==MZHealingState.REG_HEAL?MZOptions.HEALING_REGULAR_TICKS:MZOptions.HEALING_EXTRA_TICKS;
			healed.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, dur, 0));
			MZUtil.playerThink(healed, p.getName() + " just healed me!");
			MZUtil.getMZPlayer(healed.getName()).setBleeding(false);
			scheduleCooldown(healed);
			break;

		case ANTIBIOTIC_HEAL:
			MZUtil.playerThink(p, "You successfully cured " + healed.getName() + ".");
			healed.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, MZOptions.HEALING_REGULAR_TICKS, 0));
			MZUtil.playerThink(healed, p.getName() + " just cured me!");
			MZUtil.getMZPlayer(healed.getName()).setDiseased(false);
			scheduleCooldown(healed);
		default:
			break;
		}

	}
	
	public void scheduleCooldown(final Player p) {
		MZUtil.setMZHealingState(p, MZHealingState.HEAL_COOLDOWN);
		Bukkit.getScheduler().runTaskLater(OpenMZ.get(), 
				new Runnable() { @Override
				public void run() {
					MZUtil.setMZHealingState(p, MZHealingState.DEFAULT);
				}}, MZOptions.HEALING_DELAY);
				
	}
	@Override
	public String getName() {
		return ChatColor.BLUE + "Medical Scissors";
	}

	@Override
	public Material getType() {
		return Material.SHEARS;
	}
	



}
