package net.falcon;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.PersistenceException;

import net.falcon.chest.MZChest;
import net.falcon.chest.MZChestTemplate;
import net.falcon.chest.MZSpawn;
import net.falcon.cmd.ChatAdapter;
import net.falcon.data.MZOptions;
import net.falcon.data.MZPlayer;
import net.falcon.item.MZAntibiotics;
import net.falcon.item.MZAntidote;
import net.falcon.item.MZBandage;
import net.falcon.item.MZGrapplingHook;
import net.falcon.item.MZItemTemplate;
import net.falcon.item.MZMedicalScissors;
import net.falcon.item.MZOintment;
import net.falcon.listen.MZEventExecutor;
import net.falcon.listen.MZPlayerListener;
import net.falcon.util.MZUtil;
import net.falcon.util.Util;
import net.falcon.zombie.MPigZombie;
import net.falcon.zombie.MZZombieListener;
import net.falcon.zombie.MZombie;
import net.minecraft.server.v1_7_R3.Item;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;

public class OpenMZ extends JavaPlugin implements Listener {

	public static OpenMZ instance;
	public static MZZombieListener listener = new MZZombieListener();
	public static MZPlayerListener pListener = new MZPlayerListener();
	public static MZEventExecutor executor = new MZEventExecutor();

	private final HashMap<String, MZPlayer> mzPlayerMap = new HashMap<String,MZPlayer>();
	public final ArrayList<MZChestTemplate> chestTemplates = new ArrayList<MZChestTemplate>();
	private ArrayList<MZChest> respawnChests = new ArrayList<MZChest>();
	public final HashMap<Location, MZChest> allChests = new HashMap<Location, MZChest>();
	public final HashMap<String, MZSpawn> spawns = new HashMap<String, MZSpawn>();

	public final ArrayList<MZItemTemplate> customItems = new ArrayList<MZItemTemplate>();

	public OpenMZ() {
		instance = this;
	}

	public static OpenMZ get() {
		return instance;
	}

	//TODO known bug: players who leave for the first time throw an SQL error. It does not happen again.
	//TODO known bug: current versions of R.3 don't work.
	//TODO look into zombie spawning
	//TODO bags
	//TODO MZombie on tick is slow
	//TODO giants
	//TODO custom item Api?
	//TODO spawninv armor spawns automatically
	//TODO zombies spawn when you enter a town

	
	@Override
	public void onEnable() {
		//register all the events and stuff
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(listener, this);
		Bukkit.getPluginManager().registerEvents(pListener, this);
		Bukkit.getPluginManager().registerEvents(executor, this);

		ProtocolLibrary.getProtocolManager().addPacketListener(new ChatAdapter());
		//load up the config files
		MZOptions.init();
		MZStrings.init();
		saveConfig();
		
		//set all the max stack sizes
		for(String i : MZOptions.maxStack.keySet()) {
			Util.modifyMaxStack(Item.d(Material.getMaterial(i).getId()), MZOptions.maxStack.get(i));
		}
		

		//simple check if database exists; if not, install it
		try {
			getDatabase().find(MZPlayer.class).findRowCount();
		} catch (PersistenceException ex) {
			Util.log("No database found, installing one.");
			installDDL();
		}

		//start the scheduler
		new Thread(new MZScheduler()).start();

		//load all the chest templates
		File folder = new File(getDataFolder(), "/chest-templates/");
		if(folder.listFiles() == null) {
			folder.mkdirs();
		}
		List<File> subFiles = Arrays.asList(folder.listFiles());
		for(File f : subFiles) {
			if(f.isDirectory()) {
				continue;
			}
			if(!f.getName().endsWith(".chest")) {
				continue;
			}
			chestTemplates.add(MZChestTemplate.loadChestTemplateFromFile(f));
		}

		//load spawns from db
		List<MZSpawn> dbSpawn = getDatabase().find(MZSpawn.class).findList();
		for(MZSpawn s : dbSpawn) {
			if(s == null) {
				continue;
			}
			spawns.put(s.getSpawnName(), s);
		}



		//load all chests from DB
		List<MZChest> dbChest = getDatabase().find(MZChest.class).findList();

		if(dbChest == null) {
			return;
		}
		for(MZChest c : dbChest) {
			allChests.put(c.getLocation(), c);
		}

		for(MZChest c : allChests.values()) {
			c.respawn();
		}
		
		//add in custom zombies, pig zombies
		MZUtil.initCustomEntities();
		for(World w : Bukkit.getWorlds()) {
			MZUtil.replaceZombies(w);
		}

		//load custom items
		customItems.addAll(Util.getCustomItems());
		customItems.add(new MZBandage());
		customItems.add(new MZAntidote());
		customItems.add(new MZGrapplingHook());
		customItems.add(new MZMedicalScissors());
		customItems.add(new MZOintment());
		customItems.add(new MZAntibiotics());
	}




	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(MZPlayer.class);
		list.add(MZChest.class);
		list.add(MZSpawn.class);
		return list;
	}

	@Override
	public void onDisable() {

		//save options, and config
		MZOptions.save();
		saveConfig();
		
		//save player metadata
		try {
			for(MZPlayer p : mzPlayerMap.values()) {
				getDatabase().save(p);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//save chests
		try {
			for(MZChest c : allChests.values()) {
			//	getDatabase().save(c);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		//save chest templates
		try {
			for(MZChestTemplate t : chestTemplates) {
				MZChestTemplate.writeChestTemplate(t);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		//save spawns
		try {
			for(MZSpawn s : spawns.values()) {
				getDatabase().save(s);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args ) {
		if(!(sender instanceof Player)) { //sent from console
			return true;
		}

		Player origin = (Player)sender;
		if(!command.getName().equals("omz")) {
			return false;
		}

		if(args.length == 0) { //no additional arguments, just "/omz"
			origin.sendMessage(MZStrings.CREDITS);
			MZUtil.tellRaw(origin, MZUtil.constructLink("http://dev.bukkit.org/bukkit-plugins/open-minez/", "Made by FelonFalcon -- BukkitDev Link"), true);
			return true;
		}
		
		
		if(args[0].equals("zed")) {
			MZombie.spawn(origin.getLocation());
		}
		
		if(args[0].equals("pzed")) {
			MPigZombie.spawn(origin.getLocation());
		}

		
		if(args[0].equals("help")) {
			origin.sendMessage("-OpenMineZ Help-");
			origin.sendMessage("/omz spawn: spawn somewhere random on the map.");
			if(!origin.hasPermission(MZStrings.PERMISSION_COMMAND)) {
				return true;
			}
			origin.sendMessage("/omz config: Configure the plugin.");
			MZUtil.tellRaw(origin, MZUtil.constructLink( MZStrings.HIDECHAT_BYPASS + "http://dev.bukkit.org/bukkit-plugins/open-minez/", "Click here to get advanced help."), true);
		}

		if(args[0].equals("spawn")) {
			if(spawns.size() == 0) {
				origin.sendMessage("No spawns have been set for this server.");
				return true;
			}
			
			if(args.length == 1)  {//just /omz spawn
				//reset the player
				MZUtil.giveSpawnInventory(origin);
				MZPlayer mz = MZUtil.getMZPlayer(origin.getName());
				mz.setThirst(MZOptions.MAX_THIRST);
				mz.setBleeding(false);
				mz.setDiseased(false);
				mz.setHumanity(0);
				Set<String> spawnSet = spawns.keySet();
				String[] spawnArr = spawnSet.toArray(new String[spawnSet.size()]);
				MZSpawn randSpawn = spawns.get(spawnArr[new Random().nextInt(spawnArr.length)]);
				origin.teleport(randSpawn.getLocation());
				return true;
			}

			if(args.length >= 2 && !origin.hasPermission(MZStrings.PERMISSION_SPAWN_CHOICE)) {
				origin.sendMessage("You don't have permission to pick your spawn.");
				return true;
			}
			
			if(args.length == 2 && args[1].equalsIgnoreCase("list")) {
				origin.sendMessage("-Possible Spawns-");
				for(String s : spawns.keySet()) {
					origin.sendMessage(s);
				}
				return true;
			} else {
				MZSpawn s = spawns.get(args[1]);
				if(s == null) {
					origin.sendMessage("That spawn doesn't exist.");
					origin.sendMessage("-Possible Spawns-");
					for(String s1 : spawns.keySet()) {
						origin.sendMessage(s1);
					}
					return true;
				}
				origin.teleport(s.getLocation());
				MZUtil.giveSpawnInventory(origin);
				MZPlayer mz = MZUtil.getMZPlayer(origin.getName());
				mz.setThirst(MZOptions.MAX_THIRST);
				mz.setBleeding(false);
				mz.setDiseased(false);
				mz.setHumanity(0);
			}
		}

		if(!origin.hasPermission(MZStrings.PERMISSION_COMMAND)) {
			origin.sendMessage("You don't have permission to use advanced OpenMZ commands.");
			return true;
		}

		if(args[0].equals("bleed")) {
			MZPlayer p;
			if(args.length == 2) {
				p = MZUtil.getMZPlayer(args[1]);
				if(p == null) {
					origin.sendMessage("Player not found.");
				}
			} else {
				p = MZUtil.getMZPlayer(origin.getName());
			}
			p.setBleeding(p.isBleeding()?false:true);
		}

		if(args[0].equals("disease")) {
			MZPlayer p;
			if(args.length == 2) {
				p = MZUtil.getMZPlayer(args[1]);
				if(p == null) {
					origin.sendMessage("Player not found.");
				}
			} else {
				p = MZUtil.getMZPlayer(origin.getName());
			}
			p.setDiseased(p.isDiseased()?false:true);
		}

		if(args[0].equals("thirst")) {
			MZPlayer p;
			if(args.length == 2) {
				p = MZUtil.getMZPlayer(args[1]);
				if(p == null) {
					origin.sendMessage("Player not found.");
					return true;
				}
			} else {
				p = MZUtil.getMZPlayer(origin.getName());
			}
			p.setThirst(MZOptions.MAX_THIRST);
		}

		if(args[0].equals("config") && origin.hasPermission(MZStrings.PERMISSION_COMMAND)) {
			ChatAdapter.handleConfigCommand(origin, args);
		}
		return true;
	}

	public MZChestTemplate getChestTemplate(String name) {
		for(MZChestTemplate t : chestTemplates) {
			if (t.getId().equals(name)) {
				return t;
			}
		}
		return null;
	}

	public MZPlayer getMZPlayer(String name) {
		MZPlayer p = mzPlayerMap.get(name);
		if(p == null && Bukkit.getPlayer(name) != null) {
			p = new MZPlayer(Bukkit.getPlayer(name));
			addMZPlayer(p);
		}
		return p;
	}

	public Collection<MZPlayer> getMZPlayers() {
		return mzPlayerMap.values();
	}

	public void addMZPlayer(MZPlayer p) {
		mzPlayerMap.put(p.getPlayerName(), p);
	}

	public void removeMZPlayer(String name) {
		mzPlayerMap.remove(name);
	}

	public ArrayList<MZChest> getRespawnChests() {
		return respawnChests;
	}

	public void addChest(MZChest c) {
		allChests.put(c.getLocation(), c);
		c.scheduleRespawn();
		getDatabase().save(c);
	}

	public void removeChest(Location l) {
		MZChest mzc = allChests.get(l);
		if(mzc == null) {
			return;
		}
		getDatabase().delete(mzc);
		allChests.remove(l);
	}

	public MZChest getChest(Location l) {
		return allChests.get(l);
	}
	

	public void setRespawnChests(ArrayList<MZChest> respawnChests) {
		this.respawnChests = respawnChests;
	}

	public ArrayList<MZItemTemplate> getCustomItems() {
		return customItems;
	}
	
	







}
