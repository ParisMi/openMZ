package net.falcon.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.falcon.OpenMZ;
import net.falcon.util.Util;

import org.bukkit.configuration.file.FileConfiguration;

public class MZOptions {


	@MZConfig(category="world", description=
			"Players in survival mode can break blocks.")
	public static Boolean BREAK_BLOCKS = false; //players in survival mode can break blocks?
	
	@MZConfig(category="general", description=
			"If set to true, player zombies only drop about half of their loot.")
	public static Boolean HALF_LOOT_DROP = false;
	
	@MZConfig(category="general", description = 
			"If true, players and zombies will show blood particles when hit.")
	public static Boolean BLOOD = true;
			
	@MZConfig(category="thirst", description=
			"The amount 5-second intervals until the player loses a thirst point.")
	public static Integer THIRST_DEC_RATE = 10;

	@MZConfig(category="thirst", description=
			"Amount of half-hearts of damage the player takes for being at 0 thirst.")
	public static Integer THIRST_DAMAGE = 1;

	@MZConfig(category="thirst", description=
			"The amount of thirst the player has after drinking a water bottle.")
	public static Integer MAX_THIRST = 20;

	@MZConfig(category="bleeding", description=
			"The amount of 5 second intervals until the player gets a bleeding debuff again if they are bleeding.")
	public static Integer BLEEDING_RATE = 5;

	@MZConfig(category="bleeding", description=
			"Amount of half-hearts of damage the player takes for bleeding.")
	public static Integer BLEEDING_DAMAGE = 1;

	@MZConfig(category="bleeding", description=
			"Chance (0 - 100) of starting to bleed after taking damage.")
	public static Integer BLEEDING_CHANCE = 5;

	@MZConfig(category="bleeding", description=
			"how much a bandage heals (half hearts) if it's not used to stop bleeding.")
	public static Integer BANDAGE_HEAL = 2;

	@MZConfig(category="disease", description=
			"Chance (0 - 100) of getting the zombie disease after being hit by a zombie.")
	public static Integer DISEASE_CHANCE = 5;

	@MZConfig(category="disease", description=
			"Amount of of damage (half hearts) the player takes for being diseased.")
	public static Integer DISEASE_DAMAGE = 2;

	@MZConfig(category="disease", description=
			"Amount of 5 second intervals between getting the disease debuff.")
	public static Integer DISEASE_RATE = 8;

	@MZConfig(category="zombie", description=
			"The speed zombies move at, 100 = normal, 1 = super slow, 200 = double, etc.")
	public static Integer ZOMBIE_SPEED = 150;

	@MZConfig(category="zombie", description=
			"Amount of half-hearts of raw damage zombies do.")
	public static Integer ZOMBIE_DAMAGE = 5;

	@MZConfig(category="zombie", description=
			"Amount of half-hearts zombies start with.")
	public static Integer ZOMBIE_HEALTH = 20;

	@MZConfig(category="zombie", description=
			"The amount of blocks away a player has to be while walking for a zombie to notice them.")
	public static Integer ZOMBIE_AWARENESS_WALK = 10;

	@MZConfig(category="zombie", description=
			"")
	public static Integer PIGMAN_CHANCE = 2;

	@MZConfig(category="zombie", description=
			"")
	public static Integer PIGMAN_FOCUS_X = 0;

	@MZConfig(category="zombie", description=
			"")
	public static Integer PIGMAN_FOCUS_Z = 0;

	@MZConfig(category="zombie", description=
			"")
	public static Integer PIGMAN_RANGE = 800;
	
	@MZConfig(category="zombie", description=
			"If true, pigment that spawn will have 1/2 heart, ready to explode.")
	public static Boolean FRAGILE_PIGMEN = false;

	@MZConfig(category="zombie", description = 
			"Zombies have their own blood effect if this is set to true. If false, it uses player blood. Make sure BLOOD is true.")
	public static Boolean ZOMBIE_BLOOD = true;
	
	@MZConfig(category="zombie", description=
			"Amount of baby zombies pigmen spawn on death.")
	public static Integer PIGMAN_DEATH_BABY_AMOUNT = 4;

	@MZConfig(category="zombie", description=
			"The amount of blocks away a player has to be while sneaking for a zombie to notice them.")
	public static Integer ZOMBIE_AWARENESS_SNEAK = 5;

	@MZConfig(category="zombie", description=
			"The amount of blocks away a player has to be while sprinting for a zombie to notice them.")
	public static Integer ZOMBIE_AWARENESS_SPRINT = 30;

	@MZConfig(category="zombie", description=
			"The max amount of blocks away a zombie can be to be alerted by another zombie (see ZOMBIE_HORDE_EFFECT).")
	public static Integer ZOMBIE_HORDE_AWARENESS = 20;

	@MZConfig(category="zombie", description= 
			"When zombies notice a player, they'll alert other zombies.")
	public static Boolean ZOMBIE_HORDE_EFFECT = false;

	@MZConfig(category="chest", description=
			"If a player is nearer than this number (in blocks) to the chest, the chest won't spawn.")
	public static Integer CHEST_DISABLE_RANGE = 10;

	@MZConfig(category="chest", description=
			"If a player is nearer than this number (in blocks) to the chest, the chest only has a 25% chance of spawning."
			)
	public static Integer CHEST_DICOURAGE_RANGE = 20;

	@MZConfig(category="crafting", description=
			"If true, the durability range given to a piece of loot will cluster around" +
			"the average. If false, there is an even chance of getting loot anywhere in the durability range.")
	public static Boolean SMOOTH_DURABILITY = true;

	@MZConfig(category="crafting", description=
			"If true, tools with the EXACT same enchantment can be repaired in the 2x2 crafting box at " +
			"1/2 the normal durability bonus")
	public static Boolean REPAIR_ENCHANTS = true;

	@MZConfig(category="healing", description=
			"Ticks until a player can be healed again. 20ticks ~ 1second.")
	public static Integer HEALING_DELAY = 6000;
	
	@MZConfig(category="healing", description =
			"If true, normal minecraft HP regen rules apply. If false, health regen is disabled.")
	public static Boolean PASSIVE_REGEN_ENABLE = false;

	@MZConfig(category="healing", description=
			"How long to get regeneration when being healed normally. 50ticks ~ 1 half-heart.")
	public static Integer HEALING_REGULAR_TICKS = 300;

	@MZConfig(category="healing", description=
			"How long to get regeneration when being healed with ointment. 50ticks ~ 1 half-heart.")
	public static Integer HEALING_EXTRA_TICKS = 550;


	public static HashMap<String, Integer> maxStack = new HashMap<String, Integer>();

	public static boolean setConfig(String name, String value) {
		for(Field f : Arrays.asList( MZOptions.class.getDeclaredFields())) {
			if(f.getName().equalsIgnoreCase(name)) {
				try {
					if(f.getType().equals(Boolean.class)) {
						f.set(null, Boolean.valueOf(value));
					}
					if(f.getType().equals(Integer.class)) {
						f.set(null, Integer.valueOf(value));
					}
				} catch(Exception e) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	public static Object getConfig(String name) {
		for(Field f : Arrays.asList( MZOptions.class.getDeclaredFields())) {
			if(f.getName().equalsIgnoreCase(name)) {
				try {
					return f.get(null);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static List<String> getConfigList(String category) {
		ArrayList<String> list = new ArrayList<String>();
		for(Field f : Arrays.asList( MZOptions.class.getDeclaredFields())) {
			MZConfig mzConfig = f.getAnnotation(MZConfig.class);
			if(mzConfig != null) {
				if(mzConfig.category().equals(category) || category.equals("")) {
					list.add(f.getName());
				}
			}
		}
		return list;
	}
	
	public static MZConfig getMZConfig(String name) {
		Field f;
		try {
			f = MZOptions.class.getField(name);
		if(f == null) { return null;}
		return f.getAnnotation(MZConfig.class);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getConfigList() {
		return getConfigList("");
	}

	public static void save() {
		FileConfiguration cs = OpenMZ.get().getConfig();
		for(Field f : Arrays.asList( MZOptions.class.getDeclaredFields())) {
			MZConfig mzConfig = f.getAnnotation(MZConfig.class);
			if(mzConfig != null) {
				try {
					String path = mzConfig.category() + "." + f.getName();
					cs.set(path, f.get(null));
				} catch(Exception e) {
					e.printStackTrace();
					Util.log("Tried to save config field '" + f.getName() + "' but had a problem...");
				}
			}
		}
		for(String itemID : maxStack.keySet()) {
			cs.set("maxStacks." + itemID, maxStack.get(itemID));
		}
		

	}

	public static void init() {
		FileConfiguration cs = OpenMZ.get().getConfig();
		for(Field f : Arrays.asList( MZOptions.class.getDeclaredFields())) {
			MZConfig mzConfig = f.getAnnotation(MZConfig.class);
			if(mzConfig != null) {
				try {
					String path = mzConfig.category() + "." + f.getName();
					Object value = cs.get(path);
					if(value != null) {
						f.set(null, cs.get(path));
					} else {
						cs.set(path, f.get(null));
					}
				} catch(Exception e) {
					Util.log("Tried to set config field '" + f.getName() + "' but had a problem...");
				}
			}

		}
		if(ZOMBIE_SPEED < 10) { //fixing old way this setting worked
			ZOMBIE_SPEED = 100;
		}
		if(cs.getConfigurationSection("maxStacks") == null) {
			return;
		}
		Set<String> rawLines = cs.getConfigurationSection("maxStacks").getKeys(false);
		if(rawLines == null) {
			return;
		}
		for(String s : rawLines) {
			try {
				Integer max = cs.getInt("maxStacks." + s);
				maxStack.put(s, max);
			} catch(Exception e) {
				Util.log("Error occured when trying to load max stacks for line '" + s +".'");
				continue;
			}
		}

		OpenMZ.get().saveConfig();
		//load up options
	}
}
