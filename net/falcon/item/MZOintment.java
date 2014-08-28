package net.falcon.item;

import net.falcon.data.MZHealingState;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MZOintment extends MZItemTemplate {

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
		case REG_HEAL:
		case ANTIBIOTIC_HEAL:
			MZUtil.setMZHealingState(healed, MZHealingState.EXTRA_HEAL);
			MZUtil.playerThink(p, ChatColor.GREEN + "Applied Ointment.");
			break;
			//TODO can apply antibiotics + bonus heal all at onece?
			//TODO healing state decays if someone fails to heal you
		case HEAL_COOLDOWN:
		case DEFAULT:
			MZUtil.playerThink(p, "I need to apply a bandage first...");
		case EXTRA_HEAL:
		default:
			break;
		
		}
	}


	@Override
	public String getName() {
		return ChatColor.BLUE + "Healing Ointment";
	}

	@Override
	public Material getType() {
		return Material.INK_SACK;
	}

	@Override
	public short getDataValue() {
		return 1;
	}

}
