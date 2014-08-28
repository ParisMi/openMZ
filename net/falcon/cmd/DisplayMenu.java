package net.falcon.cmd;

import java.util.List;

import net.falcon.util.MZUtil;

import org.bukkit.entity.Player;

public class DisplayMenu {

	public static void displayMenu(DisplayMenu m, Player p) {
		MZUtil.clearChat(p, true);
		m.display(p);
		MZUtil.setMenu(p, m);
	}
	
	public void display(Player p) {
		MZUtil.clearChat(p, true);
		//draw menu here
	}
	
	public void onCommand(List<String> args, Player p) {
		
	}
}
