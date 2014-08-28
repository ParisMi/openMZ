package net.falcon.chest;

import java.util.ArrayList;
import java.util.List;

import net.falcon.MZStrings;
import net.falcon.data.MZOptions;
import net.falcon.util.MZUtil;
import net.falcon.util.Util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MZLootEntry {


	private ItemStack is;
	private Integer prob;
	private Integer duraLow;
	private Integer duraHi;

	public MZLootEntry(ItemStack itm, Integer prob) {
		this(itm,prob,100,100);
	}

	/**
	 * Creates an item entry from a formatted item the player creates.
	 * @param is
	 * @return
	 */
	public static MZLootEntry parseEntry(ItemStack is) {
		Integer prob = 0;
		Integer duraLo = 100;
		Integer duraHi = 100;
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.getLore()==null?new ArrayList<String>():im.getLore();
		List<String> loreDupe = new ArrayList<String>();
		loreDupe.addAll(lore);
		for(String s : lore) {
			if (s.contains(MZStrings.MZTRAIT_PROBABILITY)) {
				prob = Integer.parseInt(s.replace(MZStrings.MZTRAIT_PROBABILITY + ": ", ""));
			}
			if(s.contains(MZStrings.MZTRAIT_DURALOW)) {
				duraLo = Integer.parseInt(s.replace(MZStrings.MZTRAIT_DURALOW + ": ", ""));
			}
			
			if(s.contains(MZStrings.MZTRAIT_DURAHI)) {
				duraHi = Integer.parseInt(s.replace(MZStrings.MZTRAIT_DURAHI + ": ", ""));
			}
		}
		im.setLore(loreDupe);
		is.setItemMeta(im);
		MZUtil.removeMZTrait(is, MZStrings.MZTRAIT_PROBABILITY);
		MZUtil.removeMZTrait(is, MZStrings.MZTRAIT_DURALOW);
		MZUtil.removeMZTrait(is, MZStrings.MZTRAIT_DURAHI);
		return new MZLootEntry(is, prob, duraLo, duraHi);
	}

	public MZLootEntry(ItemStack itm, Integer aprob, Integer aduraLow, Integer aduraHi) {
		if(aduraLow > aduraHi) {
			Integer cln = aduraLow.intValue();
			aduraLow = duraHi;
			aduraHi = cln;
		}
		duraLow = aduraLow;
		duraHi = aduraHi;
		is = itm;
		prob = aprob;
	}

	public ItemStack generate() {
		ItemStack nis = is.clone();
		if(Util.isTool(nis)) { //tools only
			Double rand = MZOptions.SMOOTH_DURABILITY?Util.randomRangeNormal(duraLow, duraHi):Util.randomRange(duraLow, duraHi);
			Util.setDurability(nis, rand.intValue());
		}
		return nis;
	}

	public ItemStack getItem() {
		return is;
	}

	public void setItem(ItemStack is) {
		this.is = is;
	}

	public Integer getProb() {
		return prob;
	}

	public void setProb(Integer prob) {
		this.prob = prob;
	}

	public Integer getDuraLow() {
		return duraLow;
	}

	public void setDuraLow(Integer duraLow) {
		this.duraLow = duraLow;
	}

	public Integer getDuraHi() {
		return duraHi;
	}

	public void setDuraHi(Integer duraHi) {
		this.duraHi = duraHi;
	}
}
