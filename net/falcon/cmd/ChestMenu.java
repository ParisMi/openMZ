package net.falcon.cmd;

import java.util.List;

import net.falcon.MZStrings;
import net.falcon.OpenMZ;
import net.falcon.chest.MZChest;
import net.falcon.chest.MZChestTemplate;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChestMenu extends DisplayMenu {

	Boolean recentRemChg = false;
	Boolean listChests = false;
	String chestMode = "";
	@Override
	public void display(Player p) {
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "Chest Menu");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		String chestRemSetting = MZUtil.getChestPlaceSetting(p);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config back", "Go back", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config list", "List chests", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config remtoggle", "Chest Removal Mode: ", 
				chestRemSetting.equalsIgnoreCase("")?ChatColor.RED + "OFF":ChatColor.GREEN + "ON"), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config place", "Place a chest", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config create", "Create a chest", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config demo", "Demo a chest", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config respawn", "Force respawn ", "of nearby chests"), true);
		if(listChests) {
			listChests = false;
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Current Chests:");
			for(MZChestTemplate t : OpenMZ.get().chestTemplates) {
				p.sendMessage(MZStrings.HIDECHAT_BYPASS + t.getId());
			}
		}
		if(recentRemChg) {
			recentRemChg = false;
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + "To remove chests, turn on this mode, go into creative mode, and break a chest.");
		}
		if(!chestMode.equalsIgnoreCase("")) {
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Enter in a chest name to " + chestMode + ":");
		}
	}
	
	@Override
	public void onCommand(List<String> args, Player p) {
		String arg = args.get(0);
		
		if(args.get(0).equals("back")) {
			DisplayMenu.displayMenu(new MainMenu(), p);
			return;
		}
		if(arg.equalsIgnoreCase("list")) {
			listChests= true;
		}
		if(arg.equalsIgnoreCase("remtoggle")) {
			String chestRemSetting = MZUtil.getChestPlaceSetting(p);
			MZUtil.setChestPlaceSetting(p, chestRemSetting.equalsIgnoreCase("")?MZStrings.CHEST_REMOVAL_SETTING:"");
			recentRemChg = true;
			DisplayMenu.displayMenu(this, p);
			return;
		}
		
		if(arg.equals("create")) {
			DisplayMenu.displayMenu(new CreateChestMenu(), p);
			return;
		}
		
		if(arg.equalsIgnoreCase("place")) {
			chestMode = "place";
			DisplayMenu.displayMenu(this, p);
			return;
		}
		
		if(arg.equalsIgnoreCase("respawn")) {
			for(MZChest c : OpenMZ.get().allChests.values()) {
				if(c.getLocation().distanceSquared(p.getLocation()) < (50 * 50)) {
					c.forceRespawn();
				}
			}
		}
		
		if(arg.equalsIgnoreCase("demo")) {
			chestMode = "demo";
			DisplayMenu.displayMenu(this, p);
			return;
		}
		
		if(chestMode.equalsIgnoreCase("demo")) {
			MZChestTemplate t = OpenMZ.get().getChestTemplate(arg);
			p.getInventory().clear();
			p.getInventory().setContents(t.generateLootInventory().getContents());
			chestMode = "";
		}
		
		if(chestMode.equals("place")) {
			ItemStack chest = new ItemStack(Material.CHEST, 1);
			ItemMeta im = chest.getItemMeta();
			im.setDisplayName("Chest: " + arg);
			chest.setItemMeta(im);
			MZUtil.setMZTrait(chest, MZStrings.MZTRAIT_CHESTTYPE,arg);
			p.getInventory().addItem(chest);
			chestMode = "";
		}
		
		DisplayMenu.displayMenu(this, p);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}


