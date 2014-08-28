package net.falcon.item;

import net.falcon.util.Util;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class MZItemTemplate {
	
	/**
	 * Fired if the player deals damage with this item.
	 * @param e
	 */
	public void onDealDamage(EntityDamageByEntityEvent e) {}
	/**
	 * Fired if the player has this item in their hand when they're hit.
	 * @param e
	 */
	public void onTakeDamage(EntityDamageByEntityEvent e) {}
	public void onConsume(PlayerItemConsumeEvent e) {}
	public void onInteract(PlayerInteractEvent e) {}
	
	
	
	
	public String getName() {return "";}
	public Material getType() { return Material.AIR;}
	public short getDataValue() { return -1;}
	public ItemStack generateItem() {
		ItemStack s = new ItemStack(getType());
		short data = getDataValue();
		if(data != -1) {
			s.setDurability(data);
		}
		Util.renameItem(s, getName());
		return s;
	}
}
