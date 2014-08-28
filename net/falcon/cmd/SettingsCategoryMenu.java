package net.falcon.cmd;

import java.util.List;

import net.falcon.MZStrings;
import net.falcon.data.MZOptions;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SettingsCategoryMenu extends DisplayMenu {

	
	String cat;
	public SettingsCategoryMenu(String category) {
		cat = category;
	}
	
	@Override
	public void display(Player p) {
		MZUtil.clearChat(p, true);
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "Select a Setting to Change:");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config back", "Go back", ""), true);
		for(String s : MZOptions.getConfigList(cat)) {
			MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config " + s, s, ""), true);
		}
	}
	
	@Override
	public void onCommand(List<String> args, Player p) {
		if(args.get(0).equals("back")) {
			DisplayMenu.displayMenu(new SettingsMenu(), p);
			return;
		}
		
		DisplayMenu.displayMenu(new SettingsEditMenu(args.get(0)), p);
	}
}
