package net.falcon.event;

import net.falcon.data.MZPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MZDiseaseChangeEvent extends Event {

	
	private static final HandlerList handlers = new HandlerList();
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	private Player player;
	private boolean diseased;
	
	public MZDiseaseChangeEvent(MZPlayer p, Boolean isDiseased) {
		player = Bukkit.getPlayer(p.getPlayerName());
		diseased = isDiseased;
	}

	public boolean isDiseased() {
		return diseased;
	}

	public void setDiseased(boolean diseased) {
		this.diseased = diseased;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
