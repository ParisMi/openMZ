package net.falcon.listen;

import net.falcon.MZStrings;
import net.falcon.data.MZOptions;
import net.falcon.data.MZVisibility;
import net.falcon.event.MZBleedingChangeEvent;
import net.falcon.event.MZBleedingDamageEvent;
import net.falcon.event.MZDiseaseChangeEvent;
import net.falcon.event.MZDiseaseDamageEvent;
import net.falcon.event.MZThirstChangeEvent;
import net.falcon.event.MZThirstDamageEvent;
import net.falcon.event.MZVisibilityChangeEvent;
import net.falcon.util.MZUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *Acts upon the direct implications of some MZ event occuring.
 *The actual DOING of the event is done from wherever its called.
 * @author Falcon
 *
 */
public class MZEventExecutor implements Listener {

	
	@EventHandler
	public void onThirstChange(MZThirstChangeEvent e) {
		Player p = e.getPlayer();
		int newLevel = e.getNewLevel();
		e.getPlayer().setLevel(newLevel);
		if(newLevel == 10) {
			MZUtil.playerThink(p, MZStrings.LOW_THIRST);
		}
		if(newLevel == 5) {
			MZUtil.playerThink(p, MZStrings.LOWER_THIRST);
		}
		if(newLevel == 0) {
			MZUtil.playerThink(p, MZStrings.LOWEST_THIRST);
		}
	}
	
	@EventHandler
	public void onBleedingChange(MZBleedingChangeEvent e) {
		Player p = e.getPlayer();
		if(e.isBleeding()) {
			MZUtil.playerThink(p, MZStrings.BLEEDING_START);
			MZUtil.applyBleedingEffect(p);
		} else {
			MZUtil.playerThink(e.getPlayer(), MZStrings.BLEEDING_HEAL);
		}
	}
	
	@EventHandler
	public void onDiseaseChange(MZDiseaseChangeEvent e) {
		Player p = e.getPlayer();
		if(e.isDiseased()) {
			MZUtil.playerThink(p, MZStrings.DISEASE_INFECT);
			MZUtil.applyDiseaseEffect(p);
		} else {
			MZUtil.playerThink(p, MZStrings.DISEASE_HEAL);
		}
	}
	
	@SuppressWarnings("unused")
	@EventHandler
	public void onVisChange(MZVisibilityChangeEvent e) {
		e.getPlayer().setExp(e.getVis().getPercent());
		Integer radius = 0;
		if(e.getVis() == MZVisibility.LOW) {
			radius = MZOptions.ZOMBIE_AWARENESS_SNEAK;
		}
		if(e.getVis() == MZVisibility.MEDIUM) {
			radius = MZOptions.ZOMBIE_AWARENESS_WALK;
		}
		
		if(e.getVis() == MZVisibility.HIGH) {
			radius = MZOptions.ZOMBIE_AWARENESS_SPRINT;
		}
		
		
	}
	
	@EventHandler
	public void onThirstDamage(MZThirstDamageEvent e) {
		e.getPlayer().damage(MZOptions.THIRST_DAMAGE);
		MZUtil.playerThink(e.getPlayer(), MZStrings.LOWEST_THIRST);
	}
	
	@EventHandler
	public void onBleedingDamage(MZBleedingDamageEvent e) {
		e.getPlayer().damage(MZOptions.BLEEDING_DAMAGE);
		MZUtil.playerThink(e.getPlayer(), MZStrings.BLEEDING_DAMAGE);
		MZUtil.applyBleedingEffect(e.getPlayer());
	}
	
	@EventHandler
	public void onDiseaseDamage(MZDiseaseDamageEvent e) {
		e.getPlayer().damage(MZOptions.DISEASE_DAMAGE);
		MZUtil.playerThink(e.getPlayer(), MZStrings.DISEASE_DAMAGE);
		MZUtil.applyDiseaseEffect(e.getPlayer());
	}
}
