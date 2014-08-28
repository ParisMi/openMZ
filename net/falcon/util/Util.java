package net.falcon.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.falcon.OpenMZ;
import net.falcon.item.MZItemTemplate;
import net.falcon.zombie.MZombie;
import net.minecraft.server.v1_7_R3.Item;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.avaje.ebean.EbeanServer;

public class Util {


	public static final Random rand = new Random();

	public static void log(String str) {
		log(true, str);
	}

	public static void log(Boolean loud, String data) {
		if(loud) {
			OpenMZ.get().getLogger().warning(data);
		}
	}
	
	public static boolean randomChance(Integer likelihood) {
		Double d = likelihood.doubleValue()/100.0d;
		
		return d>Math.random()?true:false;
	}
	
	
	
	public static Double getDistanceXZ(int x, int z, int x1, int z1) {
		//a2 + b2 = c2
		int a = (x1 - x) * (x1 - x);
		int b = (z1 - z) * (z1 - z);
		return Math.sqrt(a + b);
	}
	
	public static String concatRestList(List<String> list, int start) {
		String result = "";
		for(int i = start; i < list.size(); i++) {
			result += list.get(i) + " ";
		}
		return result.substring(0, result.length() - 1); //get rid of extra " "
	}

	public static String concatRestArray(String[] ar, int start) {
		String result = "";
		for(int i = start; i < ar.length; i++) {
			result += ar[i] + " ";
		}
		return result.substring(0, result.length() - 1);
	}

	public static int constrainRange(int i, int min, int max) {
		return Math.max(min, Math.min(i, max));
	}

	public static int tryParseInt(String s, int def) {
		try {
			return Integer.parseInt(s);
		} catch(Exception e) {
			return def;
		}
	}

	public static boolean tryParseBoolean(String s, boolean def) {
		try {
			return Boolean.parseBoolean(s);
		} catch(Exception e) {
			return def;
		}
	}

	public static ArrayList<MZItemTemplate> getCustomItems() {
		try {
			if(true) { return new ArrayList<MZItemTemplate>(); }
			ArrayList<MZItemTemplate> templates = new ArrayList<MZItemTemplate>();
			File folder = new File(OpenMZ.get().getDataFolder(), "/custom-items/");
			if(!folder.exists()) {
				folder.mkdir();
			}
			for(File f : folder.listFiles()) {
				try {
					if(!f.getName().endsWith(".jar")) {
						continue;
					}
					
		            JarFile jarFile = new JarFile(f);
		            Enumeration<?> e = jarFile.entries();

		            URL[] url = { new URL("jar:file:" + f +"!/") };
		            ClassLoader cl = URLClassLoader.newInstance(url);

		            while (e.hasMoreElements()) {
		                JarEntry je = (JarEntry) e.nextElement();
		                if(je.isDirectory() || !je.getName().endsWith(".class")){
		                    continue;
		                }
		                String name = je.getName().replace(".class", "");
		                name = name.replace('/', '.');
		                Class<?> c = cl.loadClass(name);
		                Class<?>[] ints = c.getInterfaces();
						if(ints.length == 0 || !Arrays.asList(ints).contains(MZItemTemplate.class)) {
							Util.log("Custom Item '" + name + "' does not implement MZItemTemplate.");
						}
						Util.log("Loaded custom item " + name + ".");
						templates.add((MZItemTemplate) c.newInstance());
		            }
		            jarFile.close();
					
				}catch(Exception e) {
					e.printStackTrace();
					Util.log("Custom Item was not loaded.");
					continue;
				}

			}
			return templates;
		} catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<MZItemTemplate>();
		}
	}

	/**
	 * Returns the two tools being repaired in the crafting interface.
	 * @param ci
	 * @return
	 */
	public static ItemStack[] getRepairItems(CraftingInventory ci) {
		ItemStack[] twoRepair = new ItemStack[2];
		ItemStack[] contents = ci.getContents();
		Boolean foundFirst = false;

		//slot 0 is the result, so ignore that..yea
		for(int i = 1; i < contents.length; i++) {
			if(Util.isTool(contents[i])) {
				if(foundFirst) {
					twoRepair[1] = contents[i];
				} else {
					twoRepair[0] = contents[i];
					foundFirst = true;
				}
			}
		}
		return twoRepair;
	}

	public static boolean isEmpty(Inventory i) {
		for(ItemStack is : Arrays.asList(i.getContents())) {
			if(is == null) {
				continue;
			}
			if(is.getType() != Material.AIR) {
				return false;
			}
		}
		return true;
	}

	public static void spawnZombies(Location l, int tier) {

		//10 + (5 * tier): range
		//2 + tier: max amount
		//tier: nodes

		int maxAmt = 2 + tier;
		int maxRange = 10 + (5 * tier);
		int maxNodes = tier + 3;
		int nodeAmt = Double.valueOf((Math.random() * maxNodes)).intValue();
		//Util.log("spawning " + nodeAmt + " nodes of zombies of up to " + maxAmt + " each.");
		for(int i = 0; i < nodeAmt; i++) {
			int amt = Double.valueOf((Math.random() * maxAmt)).intValue() + 1;
			int x = l.getBlockX() + Double.valueOf((Math.random() * maxRange)).intValue();
			int z = l.getBlockZ() + Double.valueOf((Math.random() * maxRange)).intValue();

			Location spawn = Util.findOpenSpace(l.getWorld(), x, z, l.getBlockY() - 20);
			if(spawn == null) {
				continue;
			}
			for(int j = 0; j < amt; j++) {
				MZombie.spawn(spawn);
			}
		}

		return;
	}


	public static Location findOpenSpace(World w, int x, int z, int baseY) {

		Boolean found = false;

		int y = 0;
		while(!found) {
			if(w.getBlockAt(new Location(w,x,baseY + y, z)).getType() == Material.AIR) {
				if(w.getBlockAt(new Location(w,x,baseY + y + 1, z)).getType() == Material.AIR) {
					return new Location(w,x,baseY + y, z);
				}
			}
			if(y > 40) {
				found = true;
			}
			y++;
		}
		return null;
	}

	public static ArrayList<Player> getNearbyPlayers(Location l, int thresh) {
		List<Player> playerList = l.getWorld().getPlayers();
		int thresh2 = thresh * thresh;
		ArrayList<Player> nearbyPlayers = new ArrayList<Player>();
		for(Player p : playerList) {
			if(l.distanceSquared(p.getLocation()) < thresh2) {
				nearbyPlayers.add(p);
			}
		}
		return nearbyPlayers;
	}

	public static boolean areSameEnchants(ItemStack is, ItemStack is2) {
		Map<Enchantment, Integer> encMap = is.getEnchantments();
		for(Enchantment e : encMap.keySet()) {
			if(is2.getEnchantmentLevel(e) != is.getEnchantmentLevel(e)) {
				return false;
			}
		}

		encMap = is2.getEnchantments();

		for(Enchantment e : encMap.keySet()) {
			if(is2.getEnchantmentLevel(e) != is.getEnchantmentLevel(e)) {
				return false;
			}
		}

		return true;
	}

	public static EbeanServer getDatabase() {
		return OpenMZ.get().getDatabase();
	}

	public static String getItemName(ItemStack s) {
		if(s == null) {
			return "";
		}
		if(s.getItemMeta() == null) {
			return "";
		}
		return s.getItemMeta().hasDisplayName()?s.getItemMeta().getDisplayName():"";
	}

	public static void renameItem(ItemStack s, String name) {
		ItemMeta im = s.getItemMeta();
		im.setDisplayName(name);
		s.setItemMeta(im);
	}

	public static boolean isHookLanded(Fish hook) {
		//looks above 
		if(hook.getVelocity().length() > 1.2) {
			return false;
		}

		Location loc = hook.getLocation();
		World w = hook.getWorld();
		if(w.getBlockAt(loc.add(0,-.25,0)).getType().isSolid() || w.getBlockAt(loc.add(0,-.25,0)).getType() == Material.GRASS) {
			return true;
		}

		if(w.getBlockAt(loc.add(.25,-.25,0)).getType().isSolid() || w.getBlockAt(loc.add(-.25,-.25,0)).getType() == Material.GRASS) {
			return true;
		}

		if(w.getBlockAt(loc.add(0,-.25,.25)).getType().isSolid() || w.getBlockAt(loc.add(0,-.25,-.25)).getType() == Material.GRASS) {
			return true;
		}
		return false;
	}


	public static Double randomRange(Integer low, Integer high) {
		Double result = rand.nextDouble();
		result *= (high - low);
		result += low;
		return result;
	}

	public static Double randomRangeNormal(Integer low, Integer high) {
		Double randMult = rand.nextGaussian() * .8;
		randMult = Math.min(1, randMult);
		randMult = Math.max(-1, randMult);
		Double avg = high==0?0:((low.doubleValue() + high.doubleValue())/2);
		avg += (randMult * avg)/2;
		return avg;
	}


	public static void modifyMaxStack(Item item, int amount) {
		try {
			Field f = Item.class.getDeclaredField("maxStackSize");
			f.setAccessible(true);
			f.setInt(item, amount);
			Util.log("Set max item size of " + item.getName() + " to " + amount);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	//TODO unused
	public static void makeModifiable(Field nameField) throws Exception {
		nameField.setAccessible(true);
		int modifiers = nameField.getModifiers();
		Field modifierField = nameField.getClass().getDeclaredField("modifiers");
		modifiers = modifiers & ~Modifier.FINAL;
		modifierField.setAccessible(true);
		modifierField.setInt(nameField, modifiers);
	}

	public static boolean isTool(ItemStack is) {
		Material m = is.getType();
		String name = m.name();
		if(name.contains("HELMET") || name.contains("CHESTPLATE") || name.contains("LEGGINGS") || name.contains("BOOTS") ||
				name.contains("PANTS") || name.contains("PICKAXE") || name.contains("AXE") || name.contains("HOE") || name.contains("SPADE") || 
				name.contains("SWORD") || name.contains("BOW")) {
			return true;
		}
		return false;
	}
	public static void setDurability(ItemStack is, Integer percent) {
		Float porcien = (100 - percent) / 100f; //durability counts up, not down, so yea
		is.setDurability((short) (is.getType().getMaxDurability() * porcien));
	}
}
