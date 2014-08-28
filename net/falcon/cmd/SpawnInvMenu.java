package net.falcon.cmd;

import java.io.File;
import java.util.List;

import net.falcon.MZStrings;
import net.falcon.OpenMZ;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpawnInvMenu extends DisplayMenu {

	Boolean spawnsaved = false;
	
	@Override
	public void display(Player p) {
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "Spawn Inventories");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config back", "Go back", ""), true);
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Save an inventory as 'default' to have every player get one. Otherwise they'll need the omz.spawn-inv.[inv name here] permission.");
		
		for(String name : MZUtil.getSpawnInventories()) {
			MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config del " + name, ChatColor.RED + "[x] ", ChatColor.WHITE + name), true);
		}
		if(spawnsaved) {
			spawnsaved = false;
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.GREEN + "Spawn Inventory saved!");
		}
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + "To create a new spawn inventory as your current inventory, enter in a name for it here:");
	}
	
	@Override
	public void onCommand(List<String> args, Player p) {
		if(args.get(0).equals("back")) {
			DisplayMenu.displayMenu(new CommandsMenu(), p);
			return;
		}
		if(args.get(0).equals("del")) {
			File f = new File(OpenMZ.get().getDataFolder(), "/spawn-inv/" + args.get(1) + ".inv");
			f.delete();
		} else {
			MZUtil.saveSpawnInventory(args.get(0), p.getInventory());
			spawnsaved = true;
		}
		DisplayMenu.displayMenu(this, p);
	}
}
