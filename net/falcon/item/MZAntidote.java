package net.falcon.item;

import net.falcon.data.MZPlayer;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class MZAntidote extends MZItemTemplate {
	@Override
	public String getName() {
		return ChatColor.BLUE + "Antidote";
	}

	@Override
	public Material getType() {
		return Material.MILK_BUCKET;
	}


	@Override
	public void onConsume(PlayerItemConsumeEvent e) {
		MZPlayer p = MZUtil.getMZPlayer(e.getPlayer().getName());
		if(p.isDiseased()) {
			e.getPlayer().getInventory().setItemInHand(null);
			p.setDiseased(false);
			e.setCancelled(true); //otherwise it would clear pot effects
		}
	}

}
