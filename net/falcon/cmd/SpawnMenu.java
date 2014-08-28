package net.falcon.cmd;

import java.util.List;

import net.falcon.MZStrings;
import net.falcon.OpenMZ;
import net.falcon.chest.MZSpawn;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpawnMenu extends DisplayMenu {

	
	Boolean spawnsaved = false;
	
	@Override
	public void display(Player p) {
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "Spawn");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config back", "Go back", ""), true);
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Spawns:");
		for(String s : OpenMZ.get().spawns.keySet()) {
			MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config del " + s, ChatColor.RED + "[x] ", ChatColor.WHITE + s), true);
		}
		if(spawnsaved) {
			spawnsaved = false;
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.GREEN + "Spawn saved!");
		}
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Create a new spawn where you're standing by entering in a name for it here:");
	}
	
	@Override
	public void onCommand(List<String> args, Player p) {
		if(args.get(0).equals("back")) {
			DisplayMenu.displayMenu(new CommandsMenu(), p);
			return;
		}
		if(args.get(0).equals("del")) {
			MZSpawn s = OpenMZ.get().spawns.get(args.get(1));
			OpenMZ.get().getDatabase().delete(s);
			OpenMZ.get().spawns.remove(args.get(1));
		} else {
			MZSpawn s = new MZSpawn(p.getLocation(), args.get(0));
			OpenMZ.get().getDatabase().save(s);
			OpenMZ.get().spawns.put(args.get(0), s);
			spawnsaved = true;
		}
		DisplayMenu.displayMenu(this, p);
	}
}
