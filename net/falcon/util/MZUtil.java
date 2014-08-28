package net.falcon.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.falcon.MZStrings;
import net.falcon.OpenMZ;
import net.falcon.cmd.DisplayMenu;
import net.falcon.cmd.MainMenu;
import net.falcon.data.MZHealingState;
import net.falcon.data.MZPlayer;
import net.falcon.item.MZItemTemplate;
import net.falcon.zombie.MEntityType;
import net.falcon.zombie.MPigZombie;
import net.falcon.zombie.MZombie;
import net.minecraft.server.v1_7_R3.EntityPigZombie;
import net.minecraft.server.v1_7_R3.EntityTypes;
import net.minecraft.server.v1_7_R3.EntityZombie;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * All Util methods that require more than just bukkit/craftbukkit.
 * @author Sparrow
 *
 */
public class MZUtil {

	public static void playerThink(Player p, String think) {
		p.sendMessage(MZStrings.THINK_PREFIX + think);
	}

	public static MZPlayer getMZPlayer(String name) {
		return OpenMZ.get().getMZPlayer(name);
	}

	public static Player getPlayer(MZPlayer p) {
		return Bukkit.getPlayer(p.getPlayerName());
	}

	public static void tellRaw(Player p, String text) {
		tellRaw(p,text,false);
	}

	public static DisplayMenu getMenu(Player p) {
		return (DisplayMenu) MZUtil.getMetadata(p, MZStrings.METADATA_MENU, new MainMenu());
	}

	public static void setMenu(Player p, DisplayMenu m) {
		MZUtil.setMetadata(p, MZStrings.METADATA_MENU, m);
	}



	public static boolean isMZombie(Zombie z) {
		if(((CraftEntity)z).getHandle() instanceof MZombie) {
			return true;
		}
		return false;
	}

	public static void replaceZombies(World w) {
		Util.log("Loading up custom zombies for world '" + w.getName());
		net.minecraft.server.v1_7_R3.World mcWorld = ((CraftWorld) w).getHandle();
		for(Entity e : w.getEntities()) {
			net.minecraft.server.v1_7_R3.Entity cent = (((CraftEntity) e).getHandle());
			if(cent instanceof EntityPigZombie) {
				EntityPigZombie z = (EntityPigZombie)cent;
				MPigZombie mz = new MPigZombie(mcWorld);
				mz.setLocation(z.locX, z.locY, z.locZ, z.pitch, z.yaw);
				mcWorld.removeEntity(cent);
				mcWorld.addEntity(mz, SpawnReason.CUSTOM);	
				return;
			}
			if(cent instanceof EntityZombie) {
				EntityZombie z  = (EntityZombie)cent;
				MZombie mz = new MZombie(mcWorld);
				mz.setLocation(z.locX, z.locY, z.locZ, z.pitch, z.yaw);
				mcWorld.removeEntity(cent);
				mcWorld.addEntity(mz, SpawnReason.CUSTOM);	
			}
		}
	}


	public static MZombie getMZombie(Zombie z) {
		net.minecraft.server.v1_7_R3.Entity e = ((CraftEntity)z).getHandle();
		if(e instanceof MZombie) {
			return (MZombie)e;
		}
		return null;
	}

	public static void clearChat(Player p, Boolean bypass) {
		for(int i = 0; i < 35; i++) {
			p.sendMessage(bypass?MZStrings.HIDECHAT_BYPASS:"" + "                           ");
		}
	}

	public static void addLineToMissedChat(Player p, String line) {
		@SuppressWarnings("unchecked")
		ArrayList<String> missed = (ArrayList<String>) MZUtil.getMetadata(p, MZStrings.METADATA_MISSEDCHAT, new ArrayList<String>());
		missed.add(line);
	}

	@SuppressWarnings("unchecked")
	public static void catchupMissedChat(Player p) {
		ArrayList<String> missed = (ArrayList<String>) MZUtil.getMetadata(p, MZStrings.METADATA_MISSEDCHAT, new ArrayList<String>());
		for(String s : missed) {
			MZUtil.tellRaw(p, s);
		}
		missed.clear();

	}


	public static void initCustomEntities() {
		for (MEntityType entity : MEntityType.values()){
			try {
				Field c = EntityTypes.class.getDeclaredField("c");
				Field d = EntityTypes.class.getDeclaredField("d");
				Field e = EntityTypes.class.getDeclaredField("e");
				Field f = EntityTypes.class.getDeclaredField("f");
				Field g = EntityTypes.class.getDeclaredField("g");

				c.setAccessible(true);
				d.setAccessible(true);
				e.setAccessible(true);
				f.setAccessible(true);
				g.setAccessible(true);

				Map cMap = (Map) c.get(null);
				Map dMap = (Map) d.get(null);
				Map eMap = (Map) e.get(null);
				Map fMap = (Map) f.get(null);
				Map gMap = (Map) g.get(null);

				cMap.put(entity.getName(), entity.getCustomClass());
				dMap.put(entity.getCustomClass(), entity.getName());
				eMap.put(entity.getID(), entity.getCustomClass());
				fMap.put(entity.getCustomClass(), entity.getID());
				gMap.put(entity.getName(), entity.getID());

				c.set(null, cMap);
				d.set(null, dMap);
				e.set(null, eMap);
				f.set(null, fMap);
				g.set(null, gMap);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		/*
        for (BiomeBase biomeBase : BiomeBase.n()){
            if (biomeBase == null){
                break;
            }
         
            for (String field : new String[]{"as", "at", "au", "av"}){
                try{
                    Field list = BiomeBase.class.getDeclaredField(field);
                    list.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);
                    for (BiomeMeta meta : mobList){
                        for (MEntityType entity : MEntityType.values()){
                            if (entity.getNMSClass().equals(meta.b)){
                                meta.b = entity.getCustomClass();
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }*/
	}

	public static void quitConfigMode(Player p) {
		MZUtil.clearChat(p, true);
		MZUtil.setMetadata(p, MZStrings.METADATA_CONFIGMODE, false);
		MZUtil.catchupMissedChat(p);
		p.sendMessage("OMZ config menu closed.");
	}

	public static Boolean isConfigMode(Player p) {
		return (Boolean) getMetadata(p, MZStrings.METADATA_CONFIGMODE, false);
	}

	public static void toggleConfigMode(Player p) {
		Boolean bool = (Boolean) MZUtil.getMetadata(p, MZStrings.METADATA_CONFIGMODE, false);
		MZUtil.setMetadata(p, MZStrings.METADATA_CONFIGMODE, !bool);
	}


	public static void tellRaw(Player p, String text, Boolean bypass) {
		PacketContainer chat = new PacketContainer(PacketType.Play.Server.CHAT);
		chat.getChatComponents().write(0, WrappedChatComponent.fromJson(text));
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, chat);
		} catch (InvocationTargetException e) { Util.log("Chat not sent"); }
	}

	public static String constructLink(String url, String text) {
		text = ChatColor.DARK_AQUA + MZStrings.HIDECHAT_BYPASS + text + ChatColor.RESET;
		return  "{text:\"" + text + "\",clickEvent:{action:open_url,value:\"" + url + "\"}}";
		//return "{text:\"" + text + "\",clickEvent:{action:open_url,value:\"http://" + url + "\"}}";
	}


	public static String constructCommandLink(String cmd, String text, String addlText) {
		text = ChatColor.DARK_AQUA + text + ChatColor.RESET;
		return "{text:" +
		"\"" + MZStrings.HIDECHAT_BYPASS + text + "\"," +
		"clickEvent:{action:run_command,value:\"" + cmd + "\"}," +
		"extra:[{text:\" " + addlText + "\"," +
		"clickEvent:{action:suggest_command,value:\"\"}}]}";

	}

	public static void setMZTrait(ItemStack is, String traitType, String value) {
		String line = traitType + ": " + value;
		if(is == null || line == null) {
			return;
		}
		removeMZTrait(is, traitType);
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.getLore()==null?new ArrayList<String>():im.getLore();
		lore.add(line);
		im.setLore(lore);
		is.setItemMeta(im);
	}

	public static void removeMZTrait(ItemStack is, String trait) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.getLore()==null?new ArrayList<String>():im.getLore();
		List<String> loreDupe = new ArrayList<String>();
		loreDupe.addAll(lore);
		for(String s : lore) {
			if (s.contains(trait)) {
				loreDupe.remove(s);
			}
		}
		im.setLore(loreDupe);
		is.setItemMeta(im);
	}

	public static String getMZTrait(ItemStack is, String trait) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.getLore()==null?new ArrayList<String>():im.getLore();
		for(String s : lore) {
			if (s.contains(trait)) {
				return s.replace(trait + ": ", "");
			}
		}
		return "";
	}

	public static void setMZHealingState(Player p, MZHealingState s) {
		p.setMetadata(MZStrings.HEALING_STATE_PATH, new FixedMetadataValue(OpenMZ.get(), s.name()));
	}

	public static Object getMetadata(Player p, String path, Object defaul) {
		List<MetadataValue> values = p.getMetadata(path);
		if(values.size() == 0) {
			p.setMetadata(path, new FixedMetadataValue(OpenMZ.instance, defaul));
			return defaul;
		}
		if(values.size() > 1) {
			Util.log("Metadata ' " + path + " returned more than 1 value...");
		}
		return values.get(0).value();
	}

	public static void setMetadata(Player p, String path, Object val) {
		p.setMetadata(path, new FixedMetadataValue(OpenMZ.instance, val));
	}

	public static MZHealingState getMZHealingState(Player p) {
		List<MetadataValue> vals = p.getMetadata(MZStrings.HEALING_STATE_PATH);
		if(vals == null || vals.size() != 1) {
			setMZHealingState(p, MZHealingState.DEFAULT);
			return MZHealingState.DEFAULT;
		}
		return MZHealingState.valueOf(vals.get(0).asString());
	}

	public static void setSoulbound(ItemStack is, Boolean soul) {
		ItemMeta im = is.getItemMeta();
		im.setLore(im.getLore()==null?new ArrayList<String>():im.getLore());
		List<String> loreC = new ArrayList<String>();
		loreC.addAll(is.getItemMeta().getLore()==null?new ArrayList<String>():is.getItemMeta().getLore());
		for(String s : im.getLore()) {
			if(s.contains("Soulbound")) {
				loreC.remove(s);
			}
		}
		if(soul) {
			loreC.add(ChatColor.BLUE + "Soulbound");
		}
		im.setLore(loreC);
		is.setItemMeta(im);
	}

	public static boolean isSoulbound(ItemStack is) {
		if(is == null) {
			return false;
		}
		ItemMeta im = is.getItemMeta();
		if(im == null) {
			return false;
		}

		List<String> lore = im.getLore()==null?new ArrayList<String>():im.getLore();
		for(String s : lore) {
			if(s.contains("Soulbound")) {
				return true;
			}
		}
		return false;
	}

	public static Integer getMZTraitInteger(ItemStack is, String trait) {
		return Integer.parseInt(getMZTrait(is,trait));
	}

	public static boolean isMZLootEntry(ItemStack is) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.getLore();
		if(lore == null) {
			return false;
		}
		for(String s : lore) {
			if (s.contains("MZProbability")) {
				return true;
			}
		}
		return false;
	}

	public static String getChestPlaceSetting(Player p) {
		List<MetadataValue> mdV = p.getMetadata(MZStrings.CHEST_SETTING_PATH);
		if(mdV == null || mdV.size() < 1) {
			return "";
		}
		String setting = mdV.get(0).asString();
		return setting==null?"":setting;
	}

	public static void setChestPlaceSetting(Player p, String val) {
		p.setMetadata(MZStrings.CHEST_SETTING_PATH, new FixedMetadataValue(OpenMZ.get(), val));
	}

	public static void saveSpawnInventory(String name, PlayerInventory inv) {
		File f = new File(OpenMZ.get().getDataFolder(), "/spawn-inv/" + name + ".inv");
		f.getParentFile().mkdirs();

		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		int index = 0;
		for(ItemStack s : Arrays.asList(inv.getContents())) {
			//if it's armor, dont add it, it's handled separate
			if(s == null) {
				continue;
			}
			if(inv.getHelmet() != null && inv.getHelmet().equals(s)) {
				continue;
			}
			if(inv.getChestplate() != null && inv.getChestplate().equals(s)) {
				continue;
			}
			if(inv.getLeggings() != null && inv.getLeggings().equals(s)) {
				continue;
			}
			if(inv.getBoots() != null && inv.getBoots().equals(s)) {
				continue;
			}
			index++;
			fc.set("item-" + index, s);
		}
		
		ItemStack helm = inv.getHelmet();
		if(helm != null) {
			fc.set("armor-helmet", helm);
		}
		
		ItemStack chest = inv.getChestplate();
		if(chest != null) {
			fc.set("armor-chestplate", chest);
		}
		
		ItemStack pants = inv.getLeggings();
		if(pants != null) {
			fc.set("armor-leggings", pants);
		}
		
		ItemStack boots = inv.getBoots();
		if(boots != null) {
			fc.set("armor-boots", boots);
		}
		
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void loadSpawnInventory(String name, Player p) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		PlayerInventory inv = p.getInventory();
		File f = new File(OpenMZ.get().getDataFolder(), "/spawn-inv/" + name + ".inv");
		f.getParentFile().mkdirs();

		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);

		for(String s : fc.getKeys(false)) {
			Object o = fc.get(s);
			if(o instanceof ItemStack) {
				ItemStack i = (ItemStack)o;
				if(s.equalsIgnoreCase("armor-helmet")) {
					inv.setHelmet(i);
					continue;
				}
				if(s.equalsIgnoreCase("armor-chestplate")) {
					inv.setChestplate(i);
					continue;
				}
				if(s.equalsIgnoreCase("armor-leggings")) {
					inv.setLeggings(i);
					continue;
				}
				if(s.equalsIgnoreCase("armor-boots")) {
					inv.setBoots(i);
					continue;
				}
				items.add(i);
			}
		}
		p.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
	}

	public static void applyBleedingEffect(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,90,5));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,80,2));
	}

	public static void applyDiseaseEffect(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,200,5));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,80,2));
	}

	public static ArrayList<String> getSpawnInventories() {
		ArrayList<String> invFiles = new ArrayList<String>();
		File f = new File(OpenMZ.get().getDataFolder(), "/spawn-inv/");
		if(f.listFiles() == null) {
			return invFiles;
		}
		for(File fi : Arrays.asList(f.listFiles())) {
			if(fi.getName().contains(".inv")) {
				invFiles.add(fi.getName().replace(".inv", ""));
			}
		}
		return invFiles;
	}

	public static void giveSpawnInventory(Player p) {
		p.getInventory().clear();

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(String name : getSpawnInventories()) {
			if(p.hasPermission(MZStrings.PERMISSION_INVENTORY_PREFIX + name) || name.equals("default")) {
				loadSpawnInventory(name, p);
			}
		}

	}

	/**
	 * Attempts to find the item template that this custom item is based off of.
	 * If it fails, this method returns null.
	 * @param s
	 * @return
	 */
	public static MZItemTemplate getItemTemplate(ItemStack s) {
		if(s == null) {
			return null;
		}
		for(MZItemTemplate t : OpenMZ.get().getCustomItems()) {
			if(t.getName().equals(Util.getItemName(s))) {
				if(t.getType().equals(s.getType())) {
					return t;
				}
			}
		}
		return null;
	}

}
