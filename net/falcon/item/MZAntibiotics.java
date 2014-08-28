package net.falcon.item;

import net.falcon.data.MZHealingState;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MZAntibiotics extends MZItemTemplate {

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
		case EXTRA_HEAL:
		case REG_HEAL:
			MZUtil.setMZHealingState(healed, MZHealingState.ANTIBIOTIC_HEAL);
			MZUtil.playerThink(p, ChatColor.GREEN + "Applied Antibiotics.");
			break;
			//TODO can apply antibiotics + bonus heal all at onece?
			//TODO healing state decays if someone fails to heal you
		case HEAL_COOLDOWN:
		case DEFAULT:
			MZUtil.playerThink(p, "I need to apply a bandage first...");
		case ANTIBIOTIC_HEAL:
		default:
			break;
		
		}
	}


	@Override
	public String getName() {
		return ChatColor.BLUE + "Antibiotics";
	}

	@Override
	public Material getType() {
		return Material.INK_SACK;
	}

	@Override
	public short getDataValue() {
		return 10;
	}

}
