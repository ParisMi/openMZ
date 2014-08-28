package net.falcon.zombie;

import java.lang.reflect.Field;
import java.util.List;

import net.falcon.MZStrings;
import net.falcon.data.MZOptions;
import net.falcon.util.Util;
import net.minecraft.server.v1_7_R3.DamageSource;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.GenericAttributes;
import net.minecraft.server.v1_7_R3.IMonster;
import net.minecraft.server.v1_7_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_7_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_7_R3.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftZombie;
import org.bukkit.craftbukkit.v1_7_R3.util.UnsafeList;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;


public class MZombie extends net.minecraft.server.v1_7_R3.EntityZombie implements IMonster {
	//credit to http://forums.bukkit.org/threads/tutorial-increasing-pathfinding-range-of-an-entity.177678/
	private final float bw;
	public boolean hordeActive = false;

	/**
	 * Spawns a new zombie. There is a chance that this is a pigman.
	 * @param location
	 * @return
	 */
	public static Zombie spawn(Location location){
		World mcWorld = ((CraftWorld) location.getWorld()).getHandle();
		Double chanceMult = (Util.getDistanceXZ(location.getBlockX(), location.getBlockZ(), MZOptions.PIGMAN_FOCUS_X, MZOptions.PIGMAN_FOCUS_Z) / MZOptions.PIGMAN_RANGE.doubleValue());
		if(Util.randomChance((int) (MZOptions.PIGMAN_CHANCE * chanceMult))) {
			return MPigZombie.spawn(location);
		}
		MZombie customEntity = new MZombie(mcWorld);
		customEntity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		mcWorld.addEntity(customEntity, SpawnReason.CUSTOM);
		return (CraftZombie) customEntity.getBukkitEntity();
	}


	@Override
	protected void aC() {
		super.aC();
		//this.bb().b(GenericAttributes.b);
		this.getAttributeInstance(GenericAttributes.b).setValue(100.0D);

	}

	@Override
	public void setOnFire(int i) {
		return;
	}

	@Override
	public int getExpReward() {
		return 0;
	}

	@Override
	public void die(DamageSource d) {
		super.die(d);
		dropSpecialLoot();
	}

	@SuppressWarnings("rawtypes")
	public MZombie(net.minecraft.server.v1_7_R3.World world) {
		super(world);
		this.bw = (MZOptions.ZOMBIE_SPEED.floatValue() / 100.0f);
		Zombie z = (Zombie)this.getBukkitEntity();
		z.setBaby(false);
		z.setCanPickupItems(false);
		z.getEquipment().clear();
		//z.setRemoveWhenFarAway(MZOptions.ZOMBIE_DISAPPEAR_FAR); this will cause rampant spawning
		//minecraft coding...sigh. 'persistent' doesnt mean 'unlimited spawning'
		z.setMaxHealth(MZOptions.ZOMBIE_HEALTH);
		z.setHealth(MZOptions.ZOMBIE_HEALTH);
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
		this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, (this.bw) , false)); // this one to attack human
		this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, this.bw));
		this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F)); // this one to look at human
		this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
		this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 1.0D));
		this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
		this.targetSelector.a(2, new PathfinderGoalMZombie(this, EntityHuman.class, 0, true)); // this one to target human

	}

	public void dropSpecialLoot() {
		Zombie z = ((Zombie)this.getBukkitEntity());
		List<MetadataValue> invmd = z.getMetadata(MZStrings.ZOMBIE_INV_PATH);
		if(invmd == null || invmd.size() < 1) {
			return;
		}
		List<ItemStack> inv = (List<ItemStack>) ((FixedMetadataValue)invmd.get(0)).value();
		for(ItemStack i : inv) {
			if(MZOptions.HALF_LOOT_DROP && Math.random() < .5) {
				continue;
			}
			z.getWorld().dropItem(z.getLocation().add((Math.random() * 3) - 1.5, (Math.random() * 2) - 1.5, (Math.random() * 2) - 1.5), i);
		}
	}


}
