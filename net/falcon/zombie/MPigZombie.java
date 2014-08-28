package net.falcon.zombie;

import java.lang.reflect.Field;

import net.falcon.data.MZOptions;
import net.minecraft.server.v1_7_R3.DamageSource;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityPigZombie;
import net.minecraft.server.v1_7_R3.GenericAttributes;
import net.minecraft.server.v1_7_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_7_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_7_R3.World;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.util.UnsafeList;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class MPigZombie extends EntityPigZombie {
	//credit to http://forums.bukkit.org/threads/tutorial-increasing-pathfinding-range-of-an-entity.177678/
	private final float bw;
	public boolean hordeActive = false;
	
	public static PigZombie spawn(Location location){
		World mcWorld = ((CraftWorld) location.getWorld()).getHandle();
		MPigZombie customEntity = new MPigZombie(mcWorld);
		customEntity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		mcWorld.addEntity(customEntity, SpawnReason.CUSTOM);
		return (PigZombie) customEntity.getBukkitEntity();
	}


	@Override
	protected void aC() {
		super.aC();
		this.bb().b(GenericAttributes.b);
		this.getAttributeInstance(GenericAttributes.b).setValue(100.0D);

	}
	
	@Override
	public void setOnFire(int i) {
		return;
	}
	
	@Override
	public void die(DamageSource d) {
		super.die(d);
		org.bukkit.World w = this.getBukkitEntity().getWorld();
		Location l = this.getBukkitEntity().getLocation();
		w.playSound(l, Sound.EXPLODE, 1, 1);
		int i = MZOptions.PIGMAN_DEATH_BABY_AMOUNT;
		while(i > 0) {
			MZombie.spawn(l).setBaby(true);
			i--;
		}
	}

	@Override
	public int getExpReward() {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	public MPigZombie(net.minecraft.server.v1_7_R3.World world) {
		super(world);
		this.bw = (MZOptions.ZOMBIE_SPEED.floatValue() / 100.0f) * .8f; //pig zombies move at 80% zombie speed
		this.angerLevel = Integer.MAX_VALUE;
		Zombie z = (Zombie)this.getBukkitEntity();
		z.setBaby(false);
		z.setCanPickupItems(false);
		z.getEquipment().clear();
		z.setRemoveWhenFarAway(true);
		z.setMaxHealth(MZOptions.ZOMBIE_HEALTH);
		z.setHealth(MZOptions.ZOMBIE_HEALTH);
		if(MZOptions.FRAGILE_PIGMEN) {
			z.setHealth(1);
		}
		try {
			Field gsa = net.minecraft.server.v1_7_R3.PathfinderGoalSelector.class.getDeclaredField("b");
			gsa.setAccessible(true);

			gsa.set(this.goalSelector, new UnsafeList());
			gsa.set(this.targetSelector, new UnsafeList());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		this.goalSelector.a(0, new PathfinderGoalFloat(this));
		this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, (this.bw) , false)); 
		this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, this.bw));
		this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F)); 
		this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
		this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 1.0D));
		this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
		this.targetSelector.a(2, new PathfinderGoalMZombie(this, EntityHuman.class, 0, true)); 


	}


}
