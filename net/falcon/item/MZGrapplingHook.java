package net.falcon.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class MZGrapplingHook extends MZItemTemplate {

	
	//The grappling hook is a special case because it has to hook
	//PlayerFishEvent. So it's functionality is handled there

	@Override
	public String getName() {
		return ChatColor.BLUE + "Grappling Hook";
	}

	@Override
	public Material getType() {
		return Material.FISHING_ROD;
	}

}
