package net.falcon.event;

import net.falcon.data.MZPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MZBleedingChangeEvent extends Event {

	
	private static final HandlerList handlers = new HandlerList();
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	private Player player;
	private boolean bleeding;
	
	public MZBleedingChangeEvent(MZPlayer p, Boolean isBleeding) {
		player = Bukkit.getPlayer(p.getPlayerName());
		bleeding = isBleeding;
	}

	public boolean isBleeding() {
		return bleeding;
	}

	public void setBleeding(boolean bleeding) {
		this.bleeding = bleeding;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
