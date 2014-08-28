package net.falcon.listen;

import java.util.ArrayList;

import net.falcon.MZStrings;
import net.falcon.OpenMZ;
import net.falcon.chest.MZChest;
import net.falcon.chest.MZChestTemplate;
import net.falcon.cmd.ChatAdapter;
import net.falcon.data.MZOptions;
import net.falcon.data.MZPlayer;
import net.falcon.item.MZItemTemplate;
import net.falcon.util.MZUtil;
import net.falcon.util.Util;
import net.falcon.zombie.MZombie;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;


public class MZPlayerListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		MZPlayer plr = Util.getDatabase().find(MZPlayer.class).where().ieq("playerName",p.getName()).findUnique();
		if(plr == null) {
			Util.log("Did not find entry for player " + p.getName() + ". Creating one.");
			plr = new MZPlayer(p); //automatically sets up defaults
			Util.getDatabase().save(plr);
		}
		OpenMZ.get().addMZPlayer(plr);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Util.getDatabase().save(MZUtil.getMZPlayer(e.getPlayer().getName()));
		OpenMZ.get().removeMZPlayer(e.getPlayer().getName());
	}


	@EventHandler
	public void onTab(PlayerChatTabCompleteEvent e) {
		if(MZUtil.isConfigMode(e.getPlayer())) {
			MZUtil.quitConfigMode(e.getPlayer());
		}
	}

	@EventHandler
	public void onChat(final AsyncPlayerChatEvent e) {
		if(MZUtil.isConfigMode(e.getPlayer())) {
			Bukkit.getScheduler().runTaskLater(OpenMZ.get(), new Runnable() { @Override
				public void run() { ChatAdapter.handleConfigCommand(e.getPlayer(), e.getMessage().split(" ")); }}, 1	);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPotionDrink(PlayerItemConsumeEvent e) {
		final Player plr = e.getPlayer();
		final ItemStack i = e.getItem();

		//let any custom items know that they're being consumed.
		MZItemTemplate t = MZUtil.getItemTemplate(i);
		if(t != null) {
			t.onConsume(e);
		}

		//If this is an empty water bottle, make it refill their thirst
		MZPlayer p = MZUtil.getMZPlayer(e.getPlayer().getName());
		if(e.getItem().getType() == Material.POTION) {

			if(e.getItem().getDurability() == (short)0) {
				p.setThirst(MZOptions.MAX_THIRST);
				MZUtil.playerThink(e.getPlayer(), MZStrings.THIRST_REFILL);
			} else {
				Bukkit.getScheduler().runTaskLater(OpenMZ.get(), new Runnable() { 
					@Override
					public void run() { 
						Util.log("runngg");
						plr.getInventory().remove(plr.getInventory().getItemInHand());
					}}, 1	);
			}
		}
	}

	@EventHandler
	public void onFish(PlayerFishEvent e) {
		//manually handle the grappling hook logic.
		if(Util.isHookLanded(e.getHook()) && e.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("Grappling")) {
			if(e.getHook().getLocation().distance(e.getPlayer().getLocation()) < 3) {
				return;
			}
			Vector spd = e.getHook().getLocation().subtract(e.getPlayer().getLocation()).toVector();
			spd.multiply(new Vector(.20,.17,.20));
			e.getPlayer().setVelocity(e.getPlayer().getVelocity().add(spd));
		}
	}


	/**
	 * Creates possibility for bleeding
	 * @param e
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.isCancelled()) {
			return;
		}
		if(e.getEntity() instanceof Player) {
			if(MZOptions.BLOOD) {
				e.getEntity().getWorld().playEffect(e.getEntity().getLocation().add(0, 1d, 0),
						Effect.STEP_SOUND, Material.REDSTONE_WIRE);
			}
			Integer rand = (int) (Math.random() * 100);
			if( rand < MZOptions.BLEEDING_CHANCE) {
				MZUtil.getMZPlayer(((Player)e.getEntity()).getName()).setBleeding(true);
			}
		}
	}

	//kill the player, reset their stats, and spawn a loot zombie.
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		MZPlayer mz = MZUtil.getMZPlayer(e.getEntity().getName());
		mz.setBleeding(false);
		mz.setDiseased(false);
		mz.setHumanity(0);
		mz.setThirst(MZOptions.MAX_THIRST);


		Player p = e.getEntity();
		Zombie z = MZombie.spawn(p.getLocation());
		z.setMetadata(MZStrings.ZOMBIE_INV_PATH, new FixedMetadataValue(OpenMZ.get(), new ArrayList<ItemStack>(e.getDrops())));

		z.setCustomNameVisible(true);
		z.setCustomName(e.getEntity().getName());

		z.getEquipment().setBootsDropChance(0f);
		z.getEquipment().setBoots(p.getInventory().getBoots());

		z.getEquipment().setChestplateDropChance(0f);
		z.getEquipment().setChestplate(p.getInventory().getChestplate());

		z.getEquipment().setLeggingsDropChance(0f);
		z.getEquipment().setLeggings(p.getInventory().getLeggings());

		z.getEquipment().setHelmetDropChance(0f);
		z.getEquipment().setHelmet(p.getInventory().getHelmet());

		e.getDrops().clear();

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMoveItem(InventoryClickEvent e) {
		//just refreshes the player's inventory if they were fiddling with items with max stacks.
		//otherwise, the client just assumes it's stack size is correct even though the server doesn't.

		ItemStack activeItem = e.getCurrentItem();
		if(activeItem == null || activeItem.getType() == Material.AIR) {
			activeItem = e.getCursor();
		}

		if(MZOptions.maxStack.containsKey(activeItem.getType().name())) {
			final Player p = Bukkit.getPlayer(e.getWhoClicked().getName());
			Bukkit.getScheduler().runTaskLater(OpenMZ.get(), new Runnable() { @Override
				public void run() { p.updateInventory(); }}, 1	);
		}

	}

	@EventHandler
	public void onCraftPrepare(PrepareItemCraftEvent e) {
		//just implements custom repairing
		if(e.isRepair()) {
			//TODO fix enchantment repairing
			CraftingInventory ci = e.getInventory();
			ItemStack[] parts = Util.getRepairItems(ci);
			if(MZOptions.REPAIR_ENCHANTS && Util.areSameEnchants(parts[0], parts[1])) {
				if(parts[0].getEnchantments().size() == 0) {
					return;
				}
				ItemStack lessHealthItem = parts[0];
				if(parts[0].getDurability() > parts[1].getDurability()) {
					lessHealthItem = parts[1];
				}

				ItemStack result = e.getInventory().getResult();
				result.setDurability((short) (result.getDurability() + (lessHealthItem.getDurability() / (short)2)));
				result.addEnchantments(lessHealthItem.getEnchantments());
			}
		}
	}


	@EventHandler
	public void onRegainHP(EntityRegainHealthEvent e) {
		if(e.getEntityType() == EntityType.PLAYER && e.getRegainReason() == RegainReason.SATIATED && !MZOptions.PASSIVE_REGEN_ENABLE) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onXpChange(PlayerExpChangeEvent e) {
		e.setAmount(0); //never gain XP
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {

		if(e.getEntity() instanceof Player) {
			MZItemTemplate t = MZUtil.getItemTemplate(((Player)e.getEntity()).getInventory().getItemInHand());
			if(t != null) {
				t.onTakeDamage(e);
			}
		}


		if(e.getDamager() instanceof Player) {
			MZItemTemplate t = MZUtil.getItemTemplate(((Player)e.getDamager()).getInventory().getItemInHand());
			if(t != null) {
				t.onDealDamage(e);
			}
		}

		if(e.getEntity() instanceof Player && e.getEntity() instanceof Zombie && MZUtil.isMZombie((Zombie)e.getEntity())) {
			Integer rand = (int) (Math.random() * 100);
			e.setDamage(MZOptions.ZOMBIE_DAMAGE);
			if(e.getDamager().getType() == EntityType.PIG_ZOMBIE) {
				e.setDamage(MZOptions.ZOMBIE_DAMAGE * 1.5);
			}
			if( rand < MZOptions.DISEASE_CHANCE) {
				MZUtil.getMZPlayer(((Player)e.getEntity()).getName()).setDiseased(true);
			}
		}
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof Chest) {
			Chest c = (Chest)e.getInventory().getHolder();
			MZChest ch = OpenMZ.get().getChest(c.getLocation());
			if(ch != null) {
				if(Util.isEmpty(c.getInventory())) {
					ch.scheduleRespawn();
					c.getBlock().setType(Material.AIR);
					c.getBlock().breakNaturally();
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent e) {
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE && !MZOptions.BREAK_BLOCKS) {
			e.setCancelled(true);
			Bukkit.getScheduler().runTaskLater(OpenMZ.get(), new Runnable() { @SuppressWarnings("deprecation")
			@Override
			public void run() { e.getPlayer().updateInventory(); }}, 1	);
			return;
		}
		if(e.getBlock().getType() == Material.CHEST) {
			if(!e.getPlayer().hasPermission(MZStrings.PERMISSION_CHEST_PREFIX)) {
				e.getBlock().setType(Material.AIR);
				e.getPlayer().sendMessage("You need the omz.chest permission to place chests.");
				return;
			}
			String chestSetting = MZUtil.getMZTrait(e.getItemInHand(), MZStrings.MZTRAIT_CHESTTYPE);
			if(chestSetting.equals("")) {
				return;
			}
			e.setCancelled(true);
			MZChestTemplate t = OpenMZ.get().getChestTemplate(chestSetting);
			if(t != null) {
				e.getPlayer().sendMessage("Placed chest type '" + chestSetting + "'.");
				e.setCancelled(true);
				OpenMZ.get().addChest(new MZChest(chestSetting, e.getBlock().getLocation()));
			} else { 
				e.getPlayer().sendMessage(MZStrings.HIDECHAT_BYPASS + "Chest type '" + chestSetting + "' isn't valid.");
				return;
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		if(MZUtil.isSoulbound(e.getItemDrop().getItemStack())) {
			e.getItemDrop().remove();
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.IRONGOLEM_DEATH, 1, 2f);
		}
	}

	@EventHandler
	public void onItemDropNotPlayer(ItemSpawnEvent e) {
		if(MZUtil.isSoulbound(e.getEntity().getItemStack())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(MZOptions.BREAK_BLOCKS) {
			return;
		}
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	//TODO clean up...use inventoryView maybe?
	@EventHandler
	public void onInventoryEvent(InventoryClickEvent e) {
		if(e.getCurrentItem() == null) {
			return;
		}
		if(MZUtil.isSoulbound(e.getCursor()) || MZUtil.isSoulbound(e.getCurrentItem())) {
			if(e.getInventory().getType() != InventoryType.CRAFTING) {
				if(e.getSlot() == e.getRawSlot()) {
					e.setCancelled(true);
				}
				if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					e.setCancelled(true);
				}

			}
			if((e.getSlotType() != SlotType.CONTAINER && e.getSlotType() != SlotType.QUICKBAR)
					) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {

		MZItemTemplate t = MZUtil.getItemTemplate(e.getItem());
		if(t != null) {
			t.onInteract(e);
		}


		if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if(e.getClickedBlock().getType() == Material.CHEST) {

				if(MZUtil.getChestPlaceSetting(e.getPlayer()).equals(MZStrings.CHEST_REMOVAL_SETTING) && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
					OpenMZ.get().removeChest(e.getClickedBlock().getLocation());
					e.getPlayer().sendMessage("Removed chest.");
					return;
				}
				MZChest c = OpenMZ.get().getChest(e.getClickedBlock().getLocation());
				if (c == null) {
					return;
				}
				c.scheduleRespawn();
				e.getClickedBlock().setType(Material.AIR);
				e.getClickedBlock().breakNaturally();
			}
		}
	}


}
