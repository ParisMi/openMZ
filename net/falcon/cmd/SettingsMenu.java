package net.falcon.cmd;

import java.util.List;

import net.falcon.MZStrings;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SettingsMenu extends DisplayMenu {

	
	@Override
	public void display(Player p) {
		MZUtil.clearChat(p, true);
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "Settings Category Menu");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config back", "Go back", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config category:general", "General: ", 
				"General options."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config category:world", "World: ", 
				"Options relating to the world."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config category:thirst", "Thirst: ", 
				"Options relating to players' thirst."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config category:bleeding", "Bleeding: ", 
				"Options relationg to players bleeding."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config category:disease", "Disease: ", 
				"Options relating to the zombie disease players can contract."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config category:zombie", "Zombies: ", 
				"Options relating to the zombies themselves."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config category:chest", "Chest: ", 
				"Options relating to chests that spawn."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config category:crafting", "Crafting: ", 
				"Options relating to minor crafting tweaks."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config category:healing", "Healing: ", 
				"Options relating to healing other players."), true);
	}
	
	@Override
	public void onCommand(List<String> args, Player p) {
		if(args.get(0).equals("back")) {
			DisplayMenu.displayMenu(new MainMenu(), p);
			return;
		}
		if(args.get(0).contains("category:")) {
			DisplayMenu.displayMenu(new SettingsCategoryMenu(args.get(0).replace("category:", "")), p);
		}
	}
}
