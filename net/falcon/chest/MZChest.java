package net.falcon.chest;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import net.falcon.OpenMZ;
import net.falcon.data.MZOptions;
import net.falcon.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name="mz_chest")
public class MZChest {

	
	@Id
	private int id;
	
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	@NotNull
	public int x;
	
	@NotNull
	public int y;
	
	@NotNull
	public int z;
	
	@NotNull
	public String worldName;
	
	public Location getLocation() {
		return new Location(Bukkit.getWorld(worldName),x,y,z);
	}


	public boolean getDoesExist() {
		return doesExist;
	}
	
	public boolean isDoesExist() {
		return doesExist;
	}

	public void setDoesExist(boolean exists) {
		this.doesExist = exists;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	@NotNull
	public boolean doesExist = false;
	
	@NotNull
	public String templateName;
	
	public MZChest() {
	}
	
	public MZChest(String template, Location l) {
		x = l.getBlockX();
		y = l.getBlockY();
		z = l.getBlockZ();
		worldName = l.getWorld().getName();
		templateName = template;
		final MZChest c = this;
		//sets the chest spawning cycle
		Bukkit.getScheduler().runTaskLater(OpenMZ.get(), new Runnable() { 
			@Override
			public void run() { c.respawn(); }}
		, 10);
	}
	
	public MZChestTemplate getTemplate() {
		MZChestTemplate t = OpenMZ.get().getChestTemplate(templateName);
		if(t == null) {
			OpenMZ.get().removeChest(getLocation()); //remove this chest, it references a nonexistant chest type
		}
		return OpenMZ.get().getChestTemplate(templateName);
	}

	
	public void respawn() {
		Location loc = getLocation();
		Block b = loc.getWorld().getBlockAt(getLocation());
		
		if(b.getType() == Material.CHEST) {
			return; //chest already exists
		}
		
		if(Util.getNearbyPlayers(loc, MZOptions.CHEST_DISABLE_RANGE).size() > 0) {
			scheduleRespawn();
			return;
		}
		
		if(Util.getNearbyPlayers(loc, MZOptions.CHEST_DICOURAGE_RANGE).size() > 0 && Math.random() > .5) {
			scheduleRespawn();
			return;
		}
		
		
		b.setType(Material.CHEST);
		Chest ch = (Chest)b.getState();
		ch.getInventory().setContents(getTemplate().generateLootInventory().getContents());
		Util.spawnZombies(ch.getLocation(), getTemplate().getZombieTier());
		return;
	}
	
	public void forceRespawn() {
		Location loc = getLocation();
		Block b = loc.getWorld().getBlockAt(getLocation());
		b.setType(Material.CHEST);
		Chest ch = (Chest)b.getState();
		ch.getInventory().setContents(getTemplate().generateLootInventory().getContents());
		Util.spawnZombies(ch.getLocation(), getTemplate().getZombieTier());
	}
	
	public void scheduleRespawn() {
		final MZChest c = this;
		Bukkit.getScheduler().runTaskLater(OpenMZ.get(), new Runnable() { 
			@Override
			public void run() { c.respawn(); }}
		, getTemplate().getRespawnRate());
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}


	public int getZ() {
		return z;
	}


	public void setZ(int z) {
		this.z = z;
	}


	public String getWorldName() {
		return worldName;
	}


	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	
}
