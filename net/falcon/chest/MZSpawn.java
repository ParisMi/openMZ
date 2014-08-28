package net.falcon.chest;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.Bukkit;
import org.bukkit.Location;

@Entity
@Table(name="mz_spawn")
public class MZSpawn {

	@Id
	private int id;


	private int x;
	private int y;
	private int z;
	private String worldName;
	private String spawnName;

	public MZSpawn(Location l, String name) {
		x = l.getBlockX();
		y = l.getBlockY();
		z = l.getBlockZ();
		worldName = l.getWorld().getName();
		spawnName = name;
	}
	
	public MZSpawn() {
		
	}
	
	public Location getLocation() {
		return new Location(Bukkit.getWorld(worldName),x,y,z);
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
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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


	public String getSpawnName() {
		return spawnName;
	}


	public void setSpawnName(String spawnName) {
		this.spawnName = spawnName;
	}
}
