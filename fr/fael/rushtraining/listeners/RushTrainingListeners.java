package fr.fael.rushtraining.listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.fael.rushtraining.RushTraining;
import fr.fael.rushtraining.RushTrainingFunction;
import fr.fael.rushtraining.runnable.RushTrainingRunnable;
import fr.fael.rushtraining.runnable.RushTrainingRunnablePlayers;

public class RushTrainingListeners implements Listener{

	RushTraining rt;
	RushTrainingFunction rtf = new RushTrainingFunction();
	
	public RushTrainingListeners(RushTraining rt) {
		this.rt = rt;
	}
	
	public static List<Location> blockPlaced = new ArrayList<Location>();
	public static List<Location> bridgeBlockRed = new ArrayList<Location>();
	public static List<Location> bridgeBlockBlue = new ArrayList<Location>();
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(RushTrainingFunction.getState().equalsIgnoreCase("INGAME")) {
			if(!(event.getBlock().getType() == Material.SANDSTONE || event.getBlock().getType() == Material.TNT)) {
				event.setCancelled(true);
			}else {
				blockPlaced.remove(event.getBlock().getLocation());
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
			}
		}else {
			if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		bridgeBlockRed.add(event.getBlock().getLocation());
		event.getPlayer().sendMessage("add red");
		if(RushTrainingFunction.getState().equalsIgnoreCase("INGAME")) {
			if(!(event.getBlock().getType() == Material.SANDSTONE || event.getBlock().getType() == Material.TNT)) {
				event.setCancelled(true);
			}else if(event.getBlock().getType() == Material.SANDSTONE){
				blockPlaced.add(event.getBlock().getLocation());
				int slot = event.getPlayer().getInventory().getHeldItemSlot();
				event.getPlayer().getInventory().setItem(slot, new ItemStack(Material.SANDSTONE));
				event.getPlayer().updateInventory();
			}else if(event.getBlock().getType() == Material.TNT){
				blockPlaced.add(event.getBlock().getLocation());
				int slot = event.getPlayer().getInventory().getHeldItemSlot();
				event.getPlayer().getInventory().setItem(slot, new ItemStack(Material.TNT));
				event.getPlayer().updateInventory();
			}
		}else {
			if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event) throws IOException {
		Player player = event.getPlayer();
		if(RushTrainingFunction.getState().equalsIgnoreCase("Waiting") || RushTrainingFunction.getState().equalsIgnoreCase("Starting")) {
			initPlayer(player);
			event.setJoinMessage(RushTrainingFunction.getPrefix() + "§f" + player.getName() + " §7a rejoint la partie §a(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");
			player.teleport(RushTrainingFunction.getLobbyLocation());
			player.getInventory().clear();
			final File file = new File(RushTraining.getPlugin().getDataFolder(), RushTraining.getPlugin()+ "/" + player.getUniqueId() + ".yml");
			final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			final ConfigurationSection confsec = config.getConfigurationSection("kit.");
			if(confsec == null) {
				config.set("kit.sword", 0);
				config.set("kit.pickaxe", 1);
				config.set("kit.goldenApple", 2);
				config.set("kit.sandstone", 3);
				config.set("kit.tnt", 4);
				config.set("kit.lighter", 5);
			}
			config.save(file);
			RushTrainingFunction.PlaySound(player, Sound.NOTE_PLING);
			RushTrainingFunction.GiveItemJoin(player);
			if(RushTrainingFunction.hasMinPlayers() == true) {
				new RushTrainingRunnable(rt);
			}
		}else {
			player.setGameMode(GameMode.SPECTATOR);
			event.setJoinMessage(RushTrainingFunction.getPrefix() + "§f" + player.getName() + " §7a rejoint en tant que spectateur !");
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent event) throws IOException {
		Player player = event.getPlayer();
		if(RushTrainingFunction.getState().equalsIgnoreCase("Waiting")) {
			event.setQuitMessage(RushTrainingFunction.getPrefix() + "§f" + player.getName() + " §7a quitter la partie §a(" + (Bukkit.getOnlinePlayers().size()-1) + "/" + Bukkit.getMaxPlayers() + ")");
		}
		if(RushTrainingFunction.getState().equalsIgnoreCase("INGAME")) {
			event.setQuitMessage(RushTrainingFunction.getPrefix() + player.getCustomName() + " §7abandonne la partie !");
			if(RushTrainingFunction.blueTeam.contains(player)) {
				RushTrainingFunction.blueTeam.remove(player);
				if(RushTrainingFunction.blueTeam.size() == 0) {
					RushTrainingFunction.endGame("Rouge");
				}
			}
			if(RushTrainingFunction.redTeam.contains(player)) {
				RushTrainingFunction.redTeam.remove(player);
				if(RushTrainingFunction.redTeam.size() == 0) {
					RushTrainingFunction.endGame("Bleu");
				}
			}
		}
	}
	
	public static void initPlayer(Player player) {
		new RushTrainingRunnablePlayers(player);
	}
	@SuppressWarnings("unlikely-arg-type")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack it = event.getItem();
		if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			if(it == null) return;
			if(it.getType() == Material.AIR) return;
			ItemMeta im = it.getItemMeta();
			if(im.getDisplayName() == null) return;
			if(im.getDisplayName().equalsIgnoreCase("§cRouge")) {
				RushTrainingFunction.JoinTeam(player, "Rouge");
			}
			if(im.getDisplayName().equalsIgnoreCase("§9Bleu")) {
				RushTrainingFunction.JoinTeam(player, "Bleu");
			}
			if(im.getDisplayName().equalsIgnoreCase("§6Edit Kit")) {
				Inventory inv = Bukkit.createInventory(null, 9, "§6Edit Kit §7- §9" + player.getName());
				

				final File file = new File(RushTraining.getPlugin().getDataFolder(), RushTraining.getPlugin()+ "/" + player.getUniqueId() + ".yml");
				final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				final ConfigurationSection confsec = config.getConfigurationSection("kit.");
				final int swordSlot = confsec.getInt("sword");
				final int pickaxeSlot = confsec.getInt("pickaxe");
				final int sandstoneSlot = confsec.getInt("sandstone");
				final int gappleSlot = confsec.getInt("goldenApple");
				final int tntSlot = confsec.getInt("tnt");
				final int lighterSlot = confsec.getInt("lighter");
				inv.setItem(swordSlot, new ItemStack(Material.STONE_SWORD));
				inv.setItem(pickaxeSlot, new ItemStack(Material.IRON_PICKAXE));
				inv.setItem(gappleSlot, new ItemStack(Material.GOLDEN_APPLE));
				inv.setItem(sandstoneSlot, new ItemStack(Material.SANDSTONE));
				inv.setItem(tntSlot, new ItemStack(Material.TNT));
				inv.setItem(lighterSlot, new ItemStack(Material.FLINT_AND_STEEL));

				for(int i = 0; i < 36; i++) {
					ItemStack is = player.getInventory().getItem(i);
					if(is == null || is.equals(Material.AIR)){
						player.getInventory().setItem(i, new ItemStack(Material.BARRIER));
					}
				}
				
				player.openInventory(inv);
			}
		}
		
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(RushTrainingFunction.getState().equalsIgnoreCase("Waiting") || RushTrainingFunction.getState().equalsIgnoreCase("Starting")) {
			event.setCancelled(true);
		}
		if(!(event.getEntity() instanceof Player)) return;
		if(RushTrainingFunction.getState().equalsIgnoreCase("InGame")) {
			if(event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.FALL) {
				event.setDamage(event.getFinalDamage() / 2);
			}
		}
	}
	@EventHandler
	public void onDamageEntity(EntityDamageByEntityEvent event) {
		if(RushTrainingFunction.getState().equalsIgnoreCase("Waiting") || RushTrainingFunction.getState().equalsIgnoreCase("Starting")) {
			event.setCancelled(true);
			}
		if(RushTrainingFunction.getState().equalsIgnoreCase("InGame")) {
			Player victim = (Player) event.getEntity();
			if(event.getDamager() instanceof Player) {
				Player attacker = (Player) event.getDamager();
				if(!(attacker instanceof Player)) return;
				if(!(victim instanceof Player)) return;
				if(RushTrainingFunction.redTeam.contains(attacker) && RushTrainingFunction.redTeam.contains(victim)) event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent event) throws IOException {
		if(RushTrainingFunction.getState().equalsIgnoreCase("Waiting") || RushTrainingFunction.getState().equalsIgnoreCase("Starting")) {
			Player player = (Player)event.getWhoClicked();
			Inventory inv = event.getClickedInventory();
			if(player.getGameMode() != GameMode.CREATIVE && !(inv != null && inv.getName().contains("Edit Kit"))) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onInvClose(InventoryCloseEvent event) throws IOException {
		Player player = (Player) event.getPlayer();
		if(RushTrainingFunction.getState().equalsIgnoreCase("Waiting") || RushTrainingFunction.getState().equalsIgnoreCase("Starting")) {
			InventoryView inv = event.getPlayer().getOpenInventory();
			String invName = inv.getTitle();
			if(invName == null) return;
			if(invName.contains("Edit Kit")) {
				final File file = new File(RushTraining.getPlugin().getDataFolder(), RushTraining.getPlugin()+ "/" + player.getUniqueId() + ".yml");
				final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				for(int i = 0; i < 9; i++) {
					ItemStack is = inv.getItem(i);
					if(is != null) {
						if(is.getType().equals(Material.STONE_SWORD)) {
							config.set("kit.sword", i);
						}
						if(is.getType().equals(Material.IRON_PICKAXE)) {
							config.set("kit.pickaxe", i);
						}
						if(is.getType().equals(Material.GOLDEN_APPLE)) {
							config.set("kit.goldenApple", i);
						}
						if(is.getType().equals(Material.SANDSTONE)) {
							config.set("kit.sandstone", i);
						}
						if(is.getType().equals(Material.TNT)) {
							config.set("kit.tnt", i);
						}
						if(is.getType().equals(Material.FLINT_AND_STEEL)) {
							config.set("kit.lighter", i);
						}
					}
				}
				config.save(file);
			}
			for(int i = 0; i < 36; i++) {
				if(player.getInventory().getItem(i) != null) {
					ItemStack is = player.getInventory().getItem(i);
					String ia = is.toString();
					if(ia.contains("BARRIER")){
						player.getInventory().setItem(i, new ItemStack(Material.AIR));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if(RushTrainingFunction.getState().equalsIgnoreCase("Waiting") || RushTrainingFunction.getState().equalsIgnoreCase("Starting")) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		if(RushTrainingFunction.getState().equalsIgnoreCase("InGame")) {
			if(event.getItem().getType() == Material.GOLDEN_APPLE) {
				int slot = player.getInventory().getHeldItemSlot();
				ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE);
				new BukkitRunnable() {
					double i = 0;
					public void run() {
						player.getInventory().setItem(slot, gapple);
						player.updateInventory();
						i++;
						if(i == 1) cancel();
					}
				}.runTaskTimer(RushTraining.getPlugin(), 0, 1);
			}
		}
	}
	
	private Pattern pat = Pattern.compile("^[@]");
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();
		if(RushTrainingFunction.getState().equalsIgnoreCase("Waiting") || RushTrainingFunction.getState().equalsIgnoreCase("Starting") || RushTrainingFunction.getState().equalsIgnoreCase("FINISH")) {
			if(player.hasPermission("Moderator")) {
				Bukkit.broadcastMessage("§8[§4*§8] §7" + player.getName() + "§8: §f" + event.getMessage());
			}else {
				Bukkit.broadcastMessage("§7" + player.getName() + "§8: §7" + event.getMessage());
			}
		}else {
			String message = event.getMessage();
			if(pat.matcher(message).find()) {
				if(RushTrainingFunction.redTeam.contains(player)) {
					Bukkit.broadcastMessage("§7[§bGlobal§7] §c"+ player.getName() + "§8: §7" + message);
				}
				if(RushTrainingFunction.blueTeam.contains(player)) {
					Bukkit.broadcastMessage("§7[§bGlobal§7] §9"+ player.getName() + "§8: §7" + message);
				}
			}else {
				if(RushTrainingFunction.redTeam.contains(player)) {
					for(Player team : RushTrainingFunction.redTeam) {
						team.sendMessage("§7[§cÉquipe§7] §c"+ player.getName() + "§8: §7" + event.getMessage());
					}
				}
				if(RushTrainingFunction.blueTeam.contains(player)) {
					for(Player team : RushTrainingFunction.blueTeam) {
						team.sendMessage("§7[§9Équipe§7] §9"+ player.getName() + "§8: §7" + event.getMessage());
					}
				}
			}
		}
	}
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		Player victim = (Player)event.getEntity();
		EntityDamageEvent e = event.getEntity().getLastDamageCause();
		if(!(e instanceof EntityDamageByEntityEvent)) {
			return;
		}
		EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent)e;
		if(!(nEvent.getDamager() instanceof Player)) {
			return;
		}
		Player killer = (Player)nEvent.getDamager();
		Bukkit.broadcastMessage(RushTrainingFunction.getPrefix() + killer.getCustomName() + " §7a tué " + victim.getCustomName());
	}
	@SuppressWarnings("unlikely-arg-type")
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		Player player = (Player)event.getEntity();
		if(player.getLastDamageCause().equals(DamageCause.ENTITY_EXPLOSION)) event.setDeathMessage(RushTrainingFunction.getPrefix() + player.getCustomName() + " §7est mort à cause d'une explosion.");
		if(player.getLastDamageCause().equals(DamageCause.FALL)) event.setDeathMessage(RushTrainingFunction.getPrefix() + player.getCustomName() + " §7est mort d'une chute");
		player.spigot().respawn();
		RushTrainingFunction.playerDie(player);
		event.getDrops().clear();
	}
}
