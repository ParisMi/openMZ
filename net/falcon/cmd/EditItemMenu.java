package net.falcon.cmd;

import java.util.List;

import net.falcon.MZStrings;
import net.falcon.util.MZUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditItemMenu extends DisplayMenu {

	
	CreateChestMenu parent;
	ItemStack item;
	String editSetting = "";
	Boolean error = false;
	
	public EditItemMenu(CreateChestMenu paren, ItemStack ite) {
		parent = paren;
		item = ite;
	}
	
	
	@Override
	public void display(Player p) {
		item = p.getItemInHand();
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "Editing " + item.getType().name());
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		MZUtil.tellRaw(p, MZUtil.constructLink("http://dev.bukkit.org/bukkit-plugins/open-minez/pages/help/", "Need help?"), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config back", "Go back", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config prob", "Probability: ", 
				editString("prob") + MZUtil.getMZTrait(item, MZStrings.MZTRAIT_PROBABILITY)), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config duralow", "Min Durability (0-100): ", 
				editString("duralow") + MZUtil.getMZTrait(item, MZStrings.MZTRAIT_DURALOW)), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config durahi", "Max Durability (0-100): ", 
				editString("durahi") + MZUtil.getMZTrait(item, MZStrings.MZTRAIT_DURAHI)), true);
		if(error) {
			error = false;
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.RED + "That's not a valid setting.");
		}
		if(!editSetting.equalsIgnoreCase("")) {
				p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Enter in a new value for this setting here (-1 to remove tag):");
		}
	}
	
	@Override
	public void onCommand(List<String> args, Player p) {
		String arg = args.get(0);
		if(arg.equals("back")) {
			DisplayMenu.displayMenu(parent, p);
			return;
		}
		
		if(arg.equalsIgnoreCase("prob") || arg.equalsIgnoreCase("duralow") || arg.equalsIgnoreCase("durahi")) {
			editSetting = arg;
			DisplayMenu.displayMenu(this, p);
			return;
		}
		try {
			Integer.valueOf(arg);
			if(editSetting.equalsIgnoreCase("prob")) {
				MZUtil.setMZTrait(item, MZStrings.MZTRAIT_PROBABILITY,arg);
				if(arg.equals("-1")) {
					MZUtil.removeMZTrait(item, MZStrings.MZTRAIT_PROBABILITY);
				}
			}
			if(editSetting.equalsIgnoreCase("duralow")) {
				MZUtil.setMZTrait(item, MZStrings.MZTRAIT_DURALOW,arg);
				if(arg.equals("-1")) {
					MZUtil.removeMZTrait(item, MZStrings.MZTRAIT_DURALOW);
				}
			}
			if(editSetting.equalsIgnoreCase("durahi")) {
				MZUtil.setMZTrait(item, MZStrings.MZTRAIT_DURAHI,arg);
				if(arg.equals("-1")) {
					MZUtil.removeMZTrait(item, MZStrings.MZTRAIT_DURAHI);
				}
			}
			
			editSetting = "";
			DisplayMenu.displayMenu(this, p);
		} catch(Exception e) {
			error = true;
			DisplayMenu.displayMenu(this, p);
		}
		
	}
	
	public String editString(String line) {
		if(line.equalsIgnoreCase(editSetting)) {
			return ChatColor.GREEN + "";
		} else {
			return ChatColor.WHITE + "";
		}
	}
}
