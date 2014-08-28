package net.falcon.cmd;


import java.util.List;

import net.falcon.MZStrings;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MainMenu extends DisplayMenu {

	
	@Override
	public void display(Player p) {
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "OpenMineZ Configuration Menu");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Words in " + ChatColor.DARK_AQUA + "aqua" + ChatColor.WHITE + " are clickable buttons.");
		MZUtil.tellRaw(p, MZUtil.constructLink("http://dev.bukkit.org/bukkit-plugins/open-minez/", "Made by FelonFalcon -- BukkitDev Link"), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config quit", "Click here to quit (or press tab).", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config chest", "Chests:", "Configure chests here."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config commands", "Useful Commands:", "Useful ingame commands to configure the plugin."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config settings", "Settings:", "Change all of OpenMZ's settings from here."), true);
	}
	
	@Override
	public void onCommand(List<String> args, Player p) {
		if(args.get(0).equals("settings")) {
			DisplayMenu.displayMenu(new SettingsMenu(), p);
		}
		if(args.get(0).equals("commands")) {
			DisplayMenu.displayMenu(new CommandsMenu(), p);
		}
		if(args.get(0).equals("chest")) {
			DisplayMenu.displayMenu(new ChestMenu(), p);
		}
		if(args.get(0).equals("personal")) {
			DisplayMenu.displayMenu(new PersonalMenu(), p);
		}
		

	}
}
