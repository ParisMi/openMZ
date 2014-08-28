package net.falcon.cmd;

import java.util.ArrayList;
import java.util.List;

import net.falcon.MZStrings;
import net.falcon.OpenMZ;
import net.falcon.chest.MZChestTemplate;
import net.falcon.chest.MZLootEntry;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateChestMenu extends DisplayMenu {

	
	String editSetting = "";
	String chestname = "example" + Double.valueOf(Math.random() * 99).intValue();
	Integer respawnrate = 6000;
	Integer minloot = 1;
	Integer maxloot = 4;
	Integer ztier = 0;
	Boolean error = false;
	//Edit the item in my hand
	//chest name:
	//respawn rate:
	//minimum loot amount:
	//max loot amount:
	//Zombie tier (0-5):
	
	@Override
	public void display(Player p) {
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "Chest Creation");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		MZUtil.tellRaw(p, MZUtil.constructLink("http://dev.bukkit.org/bukkit-plugins/open-minez/pages/help/", "Need help?"), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config back", "Go back", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config edit", "Edit the item", " in my hand"), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config chestname", "Chest Name: ", 
				editString("chestname") + "" + chestname), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config respawnrate", "Respawn Rate (ticks): ", 
				editString("respawnrate") + "" + respawnrate), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config minloot", "Minimum Item Amount: ", 
				editString("minloot") + "" + minloot), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config maxloot", "Maximum Item Amount: ", 
				editString("maxloot") + "" + maxloot), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config ztier", "Zombie Tier (0-5): ", 
				editString("ztier") + "" + ztier), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config save", "Save", ""), true);
		if(error) {
			error = false;
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.RED + "That's not a valid setting.");
		}
		if(!editSetting.equals("")) {
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Enter in a new value for this setting here:");
		}
	}
	
	public String editString(String line) {
		if(line.equalsIgnoreCase(editSetting)) {
			return ChatColor.GREEN + "";
		} else {
			return ChatColor.WHITE + "";
		}
	}
	
	@Override
	public void onCommand(List<String> args, Player p) {
		String arg = args.get(0);
		
		if(args.get(0).equals("back")) {
			DisplayMenu.displayMenu(new ChestMenu(), p);
			return;
		}
		
		if(arg.equals("edit")) {
			DisplayMenu.displayMenu(new EditItemMenu(this, p.getItemInHand()), p);
			return;
		}
		
		if(arg.equals("save")) {
			ArrayList<MZLootEntry> loots = new ArrayList<MZLootEntry>();
			for(ItemStack iss : p.getInventory()) {
				if(iss != null && MZUtil.isMZLootEntry(iss)) {
					loots.add(MZLootEntry.parseEntry(iss));
				}
			}
			OpenMZ.get().chestTemplates.add(new MZChestTemplate(chestname, respawnrate, 
					loots, minloot, maxloot, ztier));
			return;
		}
		
		if(arg.equalsIgnoreCase("chestname") || arg.equalsIgnoreCase("respawnrate") || arg.equalsIgnoreCase("minloot") ||
				arg.equalsIgnoreCase("maxloot") || arg.equalsIgnoreCase("ztier")){
			editSetting = arg;
			DisplayMenu.displayMenu(this, p);
			return;
		}
		
		//user just entered in a value
		try {
			if(editSetting.equals("ztier")) {
				ztier = Integer.valueOf(arg);
				editSetting = "";
			}
			if(editSetting.equals("minloot")) {
				minloot = Integer.valueOf(arg);
				editSetting = "";
			}
			if(editSetting.equals("maxloot")) {
				maxloot = Integer.valueOf(arg);
				editSetting = "";
			}
			if(editSetting.equals("chestname")) {
				chestname = arg;
				editSetting = "";
			}
			if(editSetting.equals("respawnrate")) {
				respawnrate = Integer.valueOf(arg);
				editSetting = "";
			}
		} catch(Exception e) {
			error = true;
		}
		DisplayMenu.displayMenu(this, p);
	}
}
