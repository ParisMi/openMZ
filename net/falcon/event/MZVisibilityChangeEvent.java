package net.falcon.event;

import net.falcon.data.MZPlayer;
import net.falcon.data.MZVisibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MZVisibilityChangeEvent extends Event{

	
	private static final HandlerList handlers = new HandlerList();
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	private Player player;
	private MZVisibility vis;
	
	public MZVisibilityChangeEvent(MZPlayer p, MZVisibility visi) {
		player = Bukkit.getPlayer(p.getPlayerName());
		vis = visi;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public MZVisibility getVis() {
		return vis;
	}

	public void setVis(MZVisibility vis) {
		this.vis = vis;
	}
}
