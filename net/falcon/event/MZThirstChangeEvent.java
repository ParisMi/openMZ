package net.falcon.event;

import net.falcon.data.MZPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MZThirstChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private Player p;
	private int newLevel;
	
	public MZThirstChangeEvent(MZPlayer pa, int newThirst) {
		p = Bukkit.getPlayer(pa.getPlayerName());
		newLevel = newThirst;
	}
	
	public Player getPlayer() {
		return p;
	}


	public int getNewLevel() {
		return newLevel;
	}

	public void setNewLevel(int newLevel) {
		this.newLevel = newLevel;
	}


}
