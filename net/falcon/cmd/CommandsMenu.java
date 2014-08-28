package net.falcon.cmd;

import java.util.List;

import net.falcon.MZStrings;
import net.falcon.data.MZOptions;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandsMenu extends DisplayMenu {

	
	
	@Override
	public void display(Player p) {
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "Configuration Commands");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config back", "Go back", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config spawn", "Spawns:", "Save/Delete/Configure spawns."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config spawninv", "Spawn Inventories:", "Save/Delete/Configure spawn inventories."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config items", "Items:", "Obtain custom items and configure max item stacks."), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config zom", "Zombie Focus:", "Set your location as the zombie focus."), true);
		//zombie focus
	}
	
	
	@Override
	public void onCommand(List<String> args, Player p) {
		String arg = args.get(0);
		
		if(args.get(0).equals("back")) {
			DisplayMenu.displayMenu(new MainMenu(), p);
			return;
		}
		
		if(arg.equals("spawn")) {
			DisplayMenu.displayMenu(new SpawnMenu(), p);
		}
		if(arg.equals("spawninv")) {
			DisplayMenu.displayMenu(new SpawnInvMenu(), p);
		}
		if(arg.equals("items")) {
			DisplayMenu.displayMenu(new ItemMenu(), p);
		}
		if(arg.equals("zom")) {
			MZUtil.tellRaw(p, MZUtil.constructLink("http://dev.bukkit.org/bukkit-plugins/open-minez/pages/help/", MZStrings.HIDECHAT_BYPASS + "Focus set. What is 'Zombie Focus?'"), true);
			MZOptions.PIGMAN_FOCUS_X = p.getLocation().getBlockX();
			MZOptions.PIGMAN_FOCUS_Z = p.getLocation().getBlockZ();
		}
	}
}
