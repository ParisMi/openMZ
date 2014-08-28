package net.falcon.event;

import net.falcon.data.MZPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MZBleedingDamageEvent extends Event {

	private Player p;
	
	
	public MZBleedingDamageEvent(MZPlayer plr) {
		p = Bukkit.getPlayer(plr.getPlayerName());
	}
	
	
	
	private static final HandlerList handlers = new HandlerList();
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public Player getPlayer() {
		return p;
	}

	public void setPlayer(Player p) {
		this.p = p;
	}
}
