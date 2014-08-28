package net.falcon.zombie;

import java.lang.reflect.Field;

import net.falcon.data.MZOptions;
import net.falcon.data.MZVisibility;
import net.falcon.util.MZUtil;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityLiving;
import net.minecraft.server.v1_7_R3.PathfinderGoalNearestAttackableTarget;

import org.bukkit.Location;

public class PathfinderGoalMZombie extends PathfinderGoalNearestAttackableTarget {

	public PathfinderGoalMZombie(EntityCreature arg0, Class<EntityHuman> arg1, int arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}
	
	@Override
	public boolean a() {
		Boolean ret = super.a();
		Long timeStart = System.currentTimeMillis();
		try {
		Field currentTargetF = PathfinderGoalNearestAttackableTarget.class.getDeclaredField("g");
		currentTargetF.setAccessible(true);
		EntityLiving currentTarget = (EntityLiving) currentTargetF.get(this);
		if(ret && currentTarget instanceof EntityHuman) {
			EntityHuman h = (EntityHuman) currentTarget;
			Location hLoc = new Location(h.getWorld().getWorld(), h.locX, h.locY, h.locZ);
			//c = creature using this AI
			Location zLoc = new Location(h.getWorld().getWorld(), c.locX, c.locY, c.locZ);
			
			Double dist = hLoc.distanceSquared(zLoc);
			MZVisibility vis = MZUtil.getMZPlayer(h.getName()).getVisibility();
			MZombie z = (MZombie) c;
			if(z.hordeActive) {
				z.hordeActive = false;
				return true;
			}
			if(vis == MZVisibility.LOW && dist > MZOptions.ZOMBIE_AWARENESS_SNEAK * MZOptions.ZOMBIE_AWARENESS_SNEAK) {
				currentTargetF.set(this, null);
				return false;
			}
			if(vis == MZVisibility.MEDIUM && dist > MZOptions.ZOMBIE_AWARENESS_WALK * MZOptions.ZOMBIE_AWARENESS_WALK) {
				currentTargetF.set(this, null);
				return false;
			}
			if(vis == MZVisibility.HIGH && dist > MZOptions.ZOMBIE_AWARENESS_SPRINT * MZOptions.ZOMBIE_AWARENESS_SPRINT) {
				currentTargetF.set(this, null);
				return false;
			}
			//Util.log("Taaaime taken: " + (System.currentTimeMillis() - timeStart));
			return true;
		} } catch(Exception e) { e.printStackTrace();}
		//Util.log("Time taken: " + (System.currentTimeMillis() - timeStart));
		return ret;
	}


}
