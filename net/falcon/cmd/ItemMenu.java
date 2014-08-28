package net.falcon.cmd;

import java.util.List;

import net.falcon.MZStrings;
import net.falcon.OpenMZ;
import net.falcon.data.MZOptions;
import net.falcon.item.MZItemTemplate;
import net.falcon.util.MZUtil;
import net.falcon.util.Util;
import net.minecraft.server.v1_7_R3.Item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemMenu extends DisplayMenu {

	
	String currentEdit = "";
	Boolean error = false;
	
	@Override
	public void display(Player p) {
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.UNDERLINE + "Custom Items and Stacks");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config back", "Go back", ""), true);
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config togglesoul", "Soulbind", " the item in my hand"), true);
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Custom Items");
		for(MZItemTemplate t : OpenMZ.get().customItems) {
			MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config get " + ChatColor.stripColor(t.getName()), t.getName(), ""), true);
		}
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + " ");
		p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Max Item Stacks:");
		
		for(String i : MZOptions.maxStack.keySet()) {
			MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config edit " + i, i, ": " + (currentEdit.equalsIgnoreCase(i)?ChatColor.GREEN:"") + MZOptions.maxStack.get(i)), true);
		}
		MZUtil.tellRaw(p, MZUtil.constructCommandLink("/omz config addmax", "Add a max stack", " with the current item and amount."), true);
		if(error) {
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + ChatColor.RED + "That's not a valid setting.");
			error = false;
		}
		if(!currentEdit.equalsIgnoreCase("")) {
			p.sendMessage(MZStrings.HIDECHAT_BYPASS + "Enter in a new max stack for " + currentEdit + ":");
		}
	}
	
	
	@Override
	public void onCommand(List<String> args, Player p) {
		String arg = args.get(0);
		if(arg.equals("back")) {
			DisplayMenu.displayMenu(new CommandsMenu(), p);
			return;
		}
		if(arg.equals("togglesoul")) {
			MZUtil.setSoulbound(p.getItemInHand(), true);
		}
		if(arg.equalsIgnoreCase("get")) {
			String name = Util.concatRestList(args, 1);
			for(MZItemTemplate t : OpenMZ.get().customItems) {
				if(ChatColor.stripColor(t.getName()).equalsIgnoreCase(name)) {
					p.getInventory().addItem(t.generateItem());
				}
			}
			return;
		}
		if(arg.equalsIgnoreCase("edit")) {
			currentEdit = args.get(1);
			DisplayMenu.displayMenu(this, p);
			return;
		}
		if(arg.equalsIgnoreCase("addmax")) {
			ItemStack item = p.getItemInHand();
			Material itemM = item.getType();
			MZOptions.maxStack.put(itemM.name(), item.getAmount());
			Util.modifyMaxStack(Item.d(itemM.getId()), item.getAmount());
			DisplayMenu.displayMenu(this, p);
			return;
		}
		//entering in a new value
		for(String i : MZOptions.maxStack.keySet()) {
			if(i.equalsIgnoreCase(currentEdit)) {
				try {
				MZOptions.maxStack.put(i, Integer.valueOf(args.get(0)));
				Util.modifyMaxStack(Item.d(Material.getMaterial(i).getId()), MZOptions.maxStack.get(i));
				currentEdit = "";
				} catch(Exception e) {
					error = true;
				}
			}
		}
		DisplayMenu.displayMenu(this, p);
	}
	//max item stacks
	//custom items
}
