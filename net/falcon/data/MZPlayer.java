package net.falcon.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import net.falcon.event.MZBleedingChangeEvent;
import net.falcon.event.MZDiseaseChangeEvent;
import net.falcon.event.MZThirstChangeEvent;
import net.falcon.event.MZVisibilityChangeEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name="mz_player")
public class MZPlayer {
	
	@Id
	private int id;
	
	@NotNull
	private String playerName;
	
	@NotNull
	private int thirst;
	
	@NotNull
	private int humanity;
	
	@NotNull
	private boolean bleeding;
	
	@NotNull
	private boolean diseased;
	
	private MZVisibility visibility;
	
	public MZPlayer() {
		
	}
	
	public MZPlayer(Player p) {
		playerName = p.getName();
		thirst = 20;
		humanity = 0;
		bleeding = false;
		diseased = false;
		visibility = MZVisibility.MEDIUM;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getPlayerName() {
		return playerName;
	}


	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}


	public int getThirst() {
		return thirst;
	}


	public void setThirst(int thirst) {
		if(thirst == this.thirst) {
			return;
		}
		MZThirstChangeEvent e = new MZThirstChangeEvent(this,thirst);
		Bukkit.getServer().getPluginManager().callEvent(e);
		this.thirst = e.getNewLevel();
	}
	
	public void reduceThirst() {
		setThirst(thirst - 1);
	}


	public int getHumanity() {
		return humanity;
	}


	public void setHumanity(int humanity) {
		this.humanity = humanity;
	}
	
	public void addHumanity(int amt) {
		humanity += amt;
	}


	public boolean isBleeding() {
		return bleeding;
	}


	public void setBleeding(boolean bleeding) {
		if(bleeding == this.bleeding) {
			return;
		}
		MZBleedingChangeEvent e = new MZBleedingChangeEvent(this,bleeding);
		Bukkit.getPluginManager().callEvent(e);
		this.bleeding = e.isBleeding();
	}


	public boolean isDiseased() {
		return diseased;
	}


	public void setDiseased(boolean diseased) {
		if(diseased == this.diseased) {
			return;
		}
		MZDiseaseChangeEvent e = new MZDiseaseChangeEvent(this,diseased);
		Bukkit.getPluginManager().callEvent(e);
		this.diseased = e.isDiseased();
	}


	public void setVisibility(MZVisibility visibility) {
		if(this.visibility == visibility) {
			return;
		}
		MZVisibilityChangeEvent e = new MZVisibilityChangeEvent(this, visibility);
		Bukkit.getPluginManager().callEvent(e);
		this.visibility = e.getVis();
	}

	public MZVisibility getVisibility() {
		return visibility==null?MZVisibility.MEDIUM:visibility;
	}
	
	

}
