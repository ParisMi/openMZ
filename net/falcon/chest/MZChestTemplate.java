package net.falcon.chest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import net.falcon.OpenMZ;
import net.falcon.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
public class MZChestTemplate {

	private String id;

	//items that can spawn + probability/durability distributions
	private int respawnRate; //number of ticks until this chest will respawn
	private ArrayList<MZLootEntry> lootPool;
	private ArrayList<Integer> randomLootList;
	private int lootLow;
	private int lootHi;
	private int zombieTier;
	
	
	public MZChestTemplate(String aid, int aresrate, ArrayList<MZLootEntry> alootPool, int alow, int ahi, int azombieTier) {
		id = aid;
		respawnRate = aresrate;
		lootPool = alootPool;
		lootLow = alow;
		lootHi = ahi;
		zombieTier = azombieTier;
		
		//build random loot list
		randomLootList = new ArrayList<Integer>();
		Integer index = 0;
		for(MZLootEntry e : lootPool) {	
			Integer prob = e.getProb();
			for(int i = 0; i < prob; i++) {
				randomLootList.add(index);
			}
			index++;
		}
	}
	
	public Inventory generateLootInventory() {
		Inventory inv = Bukkit.createInventory(null, 27);
		Integer amtOfItems = Util.randomRangeNormal(lootLow, lootHi).intValue();
		for(int i = 0; i < amtOfItems; i++) {
			MZLootEntry ent = lootPool.get(randomLootList.get(Util.rand.nextInt(randomLootList.size())));
			inv.setItem(inv.firstEmpty(), ent.generate());
		}
		
		return inv;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public static void writeChestTemplate(MZChestTemplate template) {
		File chestFile = new File(OpenMZ.get().getDataFolder(), "/chest-templates/" + template.getId() + ".chest");
		if(!chestFile.exists()) {
			chestFile.getParentFile().mkdirs();
		}
		FileConfiguration fc = YamlConfiguration.loadConfiguration(chestFile);
		fc.set("respawnRate", template.respawnRate);
		fc.set("lootRate.low", template.lootLow);
		fc.set("lootRate.high", template.lootHi);
		fc.set("zombieTier", template.zombieTier);
		Integer index = 0;
		for(MZLootEntry e : template.lootPool) {
			String base = "loot.entry" + index + ".";
			fc.set(base + "item", e.getItem());
			fc.set(base + "durability.low", e.getDuraLow());
			fc.set(base + "durability.hi", e.getDuraHi());
			fc.set(base + "probability", e.getProb());
			index++;
		}
		try {
			fc.save(chestFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static MZChestTemplate loadChestTemplateFromFile(File f) {
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		
		String id = f.getName().replace(".chest", "");
		int resRate = fc.getInt("respawnRate");
		int lootLo = fc.getInt("lootRate.low");
		int lootHi = fc.getInt("lootRate.high");
		int z = fc.getInt("zombieTier");
		
		ArrayList<MZLootEntry> loots = new ArrayList<MZLootEntry>();
		ConfigurationSection cs = fc.getConfigurationSection("loot");
		Set<String> entries = cs.getKeys(false);
		for(String s : entries) {
			ItemStack baseIs = cs.getItemStack(s + ".item");
			Integer duraLow = cs.getInt(s + ".durability.low");
			Integer duraHi = cs.getInt(s + ".durability.hi");
			Integer prob = cs.getInt(s + ".probability");
			
			loots.add(new MZLootEntry(baseIs, prob, duraLow, duraHi));
		}
		
		return new MZChestTemplate(id, resRate, loots, lootLo, lootHi, z);
	}

	public int getRespawnRate() {
		return respawnRate;
	}

	public void setRespawnRate(int respawnRate) {
		this.respawnRate = respawnRate;
	}

	public int getZombieTier() {
		return zombieTier;
	}

	public void setZombieTier(int zombieTier) {
		this.zombieTier = zombieTier;
	}
	
}
