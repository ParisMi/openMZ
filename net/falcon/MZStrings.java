package net.falcon;

import java.lang.reflect.Field;
import java.util.Arrays;

import net.falcon.data.MZString;
import net.falcon.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Singleton class holding all the useful strings 
 * (ones sent to player, and ones used internally).
 *
 */
public class MZStrings {


	public static String HEALING_STATE_PATH = "omz.heal-state";

	public static String PERMISSION_COMMAND = "omz.command";
	
	public static String THINK_PREFIX = ChatColor.BLUE + "" + ChatColor.ITALIC;
	
	public static String OMZ_WATERMARK = ChatColor.DARK_GRAY + "" + ChatColor.WHITE;
	
	//OMZ automatically removes this string when it's sent to players. However,
	//it can be used to send chat messages to players that are even in the 
	//config menu.
	public static String HIDECHAT_BYPASS = "[omzbypass]";

	public static String CREDITS = "OpenMineZ by Falcon";
	
	public static String METADATA_CONFIGMODE = "omz.hidechat";
	
	public static String METADATA_MENU = "omz.displaymenu";
	
	public static String METADATA_MISSEDCHAT = "omz.missedchat";
	
	public static String CHEST_SETTING_PATH = "omz.chest-setting";
	
	public static String CHEST_REMOVAL_SETTING = "remove-chest";
	
	public static String CHEST_PLACE_SETTING_PREFIX = "chest-";
	
	public static String ZOMBIE_INV_PATH = "omz.zombie-inv";
	
	public static String MZTRAIT_PROBABILITY = "MZProbability";
	
	public static String MZTRAIT_CHESTTYPE = "MZChest";
	
	public static String MZTRAIT_DURALOW = "MZDuraLow";
	
	public static String MZTRAIT_DURAHI = "MZDuraHi";
	
	public static String PERMISSION_PREFIX = "omz.commmand";
	
	public static String PERMISSION_SPAWN_CHOICE = "omz.choose-spawn";
	
	public static String PERMISSION_CHEST_PREFIX = "omz.chest";
	
	public static String PERMISSION_INVENTORY_PREFIX = "omz.spawninv.";

	@MZString
	public static String LOW_THIRST = "I'm getting thirsty, I should find water...";

	@MZString
	public static String LOWER_THIRST = "I really need to find some water...";

	@MZString
	public static String LOWEST_THIRST = "I need to drink now!";

	@MZString
	public static String THIRST_DAMAGE = "I'm dying of thirst!";
	
	@MZString
	public static String BLEEDING_START = "I think I'm bleeding... I need a bandage ASAP!";
	
	@MZString
	public static String BLEEDING_DAMAGE = "I'm losing blood fast...I need a bandage!";
	
	@MZString
	public static String BLEEDING_HEAL = "That should stop the blood...";
	
	@MZString
	public static String BANDAGE_HEAL = "That should do a little to help my wounds...";
	
	@MZString
	public static String THIRST_REFILL = "Ah, that's much better!";
	
	@MZString
	public static String DISEASE_INFECT = "I don't feel so good... I need an antidote...";
	
	@MZString
	public static String DISEASE_DAMAGE = "If I dont find an antidote, I'm going to end up...like them...";
	
	@MZString
	public static String DISEASE_HEAL = "I feel healthier already!";

	@MZString
	public static String HEALING_COOLDOWN = "That player can't be healed yet...";
	
	@MZString
	public static String HEALING_HELP = "";

	@MZString
	public static String HORDE_AWAKENED	= "I've awakened the horde!";
	
	public static void init() {
		FileConfiguration cs = OpenMZ.get().getConfig();
		for(Field f : Arrays.asList(MZStrings.class.getDeclaredFields())) {
			MZString mzString = f.getAnnotation(MZString.class);
			if(mzString != null) {
				try {
					String path = "string." + f.getName();
					Object value = cs.get(path);
					if(value != null) {
						f.set(null, value);
					} else {
						cs.set(path, f.get(null));
					}
				} catch(Exception e) {
					Util.log("Tried to set config field '" + f.getName() + "' but had a problem...");
				}
			}
		}
	}
}
