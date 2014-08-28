package net.falcon;

import net.falcon.data.MZOptions;
import net.falcon.data.MZPlayer;
import net.falcon.event.MZBleedingDamageEvent;
import net.falcon.event.MZDiseaseDamageEvent;
import net.falcon.event.MZThirstDamageEvent;

import org.bukkit.Bukkit;

public class MZScheduler implements Runnable {

	public static int updateCounter = 0;
	public int thirstCounter = 0;
	public int bleedingCounter = 0;
	public int diseaseCounter = 0;
	
	@Override
	public void run() {
		while(OpenMZ.get().isEnabled()) {
			updateCounter++;
			if(updateCounter % 3 == 0) {
				update5s();
			}
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Mostly used for updates of disease/thirst/bleeding
	 * Ran every 5s
	 */
	public void update5s() {
		thirstCounter++;
		bleedingCounter++;
		diseaseCounter++;
		if(thirstCounter == MZOptions.THIRST_DEC_RATE) {
			thirstCounter = 0;
			for(MZPlayer p : OpenMZ.get().getMZPlayers()) {
				if(p.getThirst() > 0) {
					p.reduceThirst();
				} else {
					MZThirstDamageEvent e = new MZThirstDamageEvent(p);
					Bukkit.getPluginManager().callEvent(e);
				}
			}
		}

		if(bleedingCounter == MZOptions.BLEEDING_RATE) {
			bleedingCounter = 0;
			for(MZPlayer p : OpenMZ.get().getMZPlayers()) {
				if(p.isBleeding()) {
					MZBleedingDamageEvent e = new MZBleedingDamageEvent(p);
					Bukkit.getPluginManager().callEvent(e);
				}
			}
		}

		if(diseaseCounter == MZOptions.DISEASE_RATE) {
			diseaseCounter = 0;
			for(MZPlayer p : OpenMZ.get().getMZPlayers()) {
				if(p.isDiseased()) {
					MZDiseaseDamageEvent e = new MZDiseaseDamageEvent(p);
					Bukkit.getPluginManager().callEvent(e);
				}
			}
		}
	}

}
