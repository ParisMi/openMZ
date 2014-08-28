package net.falcon.cmd;

import java.util.List;

import net.falcon.MZStrings;
import net.falcon.data.MZConfig;
import net.falcon.data.MZOptions;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SettingsEditMenu extends DisplayMenu {

	
	String config;
	MZConfig mzc;
	Boolean error = false;
	
	public SettingsEditMenu(String conf) {
		config = conf;
		mzc = MZOptions.getMZConfig(config);
	}
	
	
	@Override
	public void display(Player p) {
		MZUtil.clearChat(p, true);
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "Settings Edit Menu");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + config + ": " + mzc.description());
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Currently set to: " + MZOptions.getConfig(config));
		if(error) {
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.RED + "That's not a valid setting.");
			error = false;
		}
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config back", "Go back", " or enter in a new value in your chatbox:"));
	}
	
	@Override
	public void onCommand(List<String> args, Player p) {
		if(args.get(0).equals("back")) {
			DisplayMenu.displayMenu(new SettingsCategoryMenu(mzc.category()), p);
			return;
		}
		
		error = !MZOptions.setConfig(config, args.get(0));
		display(p);
	}
}
